package com.thing.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thing.item.client.BasketServiceFeignClient;
import com.thing.item.client.ClientServiceFeignClient;
import com.thing.item.domain.ElasticItem;
import com.thing.item.domain.Item;
import com.thing.item.domain.ItemPhoto;
import com.thing.item.dto.*;
import com.thing.item.exception.ItemNotFoundException;
import com.thing.item.exception.ItemPhotoSaveFailException;
import com.thing.item.exception.KakaoMapErrorException;
import com.thing.item.exception.MisMatchOwnerException;
import com.thing.item.repository.ElasticItemRepository;
import com.thing.item.repository.ItemPhotoRepository;
import com.thing.item.repository.ItemRepository;
import com.thing.item.repository.ItemRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.geo.Point;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final ItemPhotoRepository itemPhotoRepository;
    private final ClientServiceFeignClient clientServiceFeignClient;
    private final BasketServiceFeignClient basketServiceFeignClient;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final ElasticItemRepository elasticItemRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao_api_key}")
    private String KAKAO_API_KEY;
    @Value("${image.path.item}")
    private String IMAGE_ROOT_PATH;

    @Transactional
    @Override
    public Item saveItem(ItemSaveRequestDTO itemSaveRequestDTO, List<MultipartFile> itemPhotoSaveRequest) {
        Item item = itemSaveRequestDTO.toEntity();
        Point addressPoint = getAddressPoint(item.getItemAddress());
        item.setPoint(addressPoint);
        item = itemRepository.save(item);
        // 사진 저장
        savePhotos(item.getItemId(), itemPhotoSaveRequest);
        return item;
    }

    @Override
    public ItemDetailResponseDTO findItemOne(Integer itemId, Integer clientIndex) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.addView();
        ClientInfoDTO clientInfoDTO = clientServiceFeignClient.getClient(item.getOwnerId()).getData();
        // 장바구니 찜 갯수 구하기
        Integer basketCount = basketServiceFeignClient.countBasket(itemId).getData();
        // 해당 물건 찜 유무 확인
        boolean isLike = (clientIndex == -1)? false : basketServiceFeignClient.showBasket(clientIndex, itemId).getData();
        return ItemDetailResponseDTO.from(clientInfoDTO, item, basketCount, isLike);
    }

    @Override
    public Slice<ItemSimpleResponseDTO> findItemList(ItemSearchRequestDTO itemSearchRequestDTO, Integer clientIndex) {
        Pageable pageable = PageRequest.of(itemSearchRequestDTO.getPage(), 10);
        List<ElasticItem> itemList = Collections.emptyList();
        if (StringUtils.hasText(itemSearchRequestDTO.getQuery())){
            itemList = elasticItemRepository.searchItemByQuery(itemSearchRequestDTO.getQuery());
        }
        List<ItemSimpleResponseDTO> content = itemRepositoryCustom.findByItemList(itemSearchRequestDTO, pageable, itemList);
        // 장바구니 처리
        if(clientIndex != -1){
            for(ItemSimpleResponseDTO dto : content){
                dto.setIsLike(basketServiceFeignClient.showBasket(clientIndex, dto.getItemId()).getData());
            }
        }
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<ItemSimpleResponseDTO> findItemListByOwnerIndex(Integer clientIndex, int page) {
        return itemRepository.findByOwnerId(clientIndex, PageRequest.of(page, 10));
    }

    @Transactional
    @Override
    public void deleteItem(Integer itemId, Integer clientIndex) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if(!item.getOwnerId().equals(clientIndex))
            throw new MisMatchOwnerException();
        // 사진 파일 삭제 로직
        deletePhotos(itemId);

        itemRepository.delete(item);
    }

    @Transactional
    @Override
    public void modifyItem(Integer clientIndex, Integer itemId, ItemSaveRequestDTO itemSaveRequestDTO, List<MultipartFile> itemPhotoSaveRequest) throws IOException {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if(!item.getOwnerId().equals(clientIndex))
            throw new MisMatchOwnerException();

        // 사진 파일 삭제 로직
        deletePhotos(itemId);

        Point point = getAddressPoint(itemSaveRequestDTO.getItemAddress());
        itemSaveRequestDTO.setItemLongitude(point.getX());
        itemSaveRequestDTO.setItemLatitude(point.getY());

        item.modifyItemInfo(itemSaveRequestDTO);
        itemRepository.save(item);
        
        itemPhotoRepository.deleteAll(item.getPhotos());
        // 사진 저장 로직
        savePhotos(itemId, itemPhotoSaveRequest);
    }

    @Override
    public String getItemPhotoPath(Integer itemPhotoIndex) {
        ItemPhoto itemPhoto = itemPhotoRepository.findById(itemPhotoIndex).orElseThrow();
        return itemPhoto.getItemPhoto();
    }

    private void savePhotos(Integer itemId, List<MultipartFile> files) {
        List<ItemPhoto> photoList = new ArrayList<>();

        // 전달되어 온 파일이 존재할 경우
        if (!CollectionUtils.isEmpty(files)) {
            // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
            // 경로 구분자 File.separator 사용
            // 파일을 저장할 세부 경로 지정
            String path = File.separator + itemId;
            File file = new File(path);

            // 디렉터리가 존재하지 않을 경우
            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                // 디렉터리 생성에 실패했을 경우
                if (!wasSuccessful){
                    log.error("디렉터리 생성 실패");
                    throw new ItemPhotoSaveFailException();
                }
            }

            // 다중 파일 처리
            boolean isMain = true;
            try{
                for (MultipartFile multipartFile : files) {
                    // 파일의 확장자 추출
                    int position = multipartFile.getOriginalFilename().lastIndexOf(".");
                    String originalFileExtension = multipartFile.getOriginalFilename().substring(position);

                    // 확장자명이 존재하지 않을 경우 처리 x
                    if (ObjectUtils.isEmpty(originalFileExtension)) {
                        continue;
                    }

                    String fileName = UUID.randomUUID() + originalFileExtension;
                    ItemPhoto itemPhoto = ItemPhoto.builder()
                            .itemId(itemId)
                            .itemPhoto(IMAGE_ROOT_PATH + path + File.separator + fileName)
                            .isMain(isMain)
                            .build();

                    if (isMain) isMain = false;

                    // 생성 후 리스트에 추가
                    photoList.add(itemPhoto);

                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File(itemPhoto.getItemPhoto());
                    multipartFile.transferTo(file);

                    // 파일 권한 설정(쓰기, 읽기)
                    file.setWritable(true);
                    file.setReadable(true);
                }
            }catch (IOException e){
                e.printStackTrace();
                throw new ItemPhotoSaveFailException();
            }
        }

        if (photoList.size() > 0) itemPhotoRepository.saveAll(photoList);
    }

    private void deletePhotos(Integer itemId){
        String path = IMAGE_ROOT_PATH + File.separator + itemId;
        File dir = new File(path);
        if(dir.exists()){
            File[] deleteList = dir.listFiles();

            for (int j = 0; j < deleteList.length; j++) {
                deleteList[j].delete();
            }

            if(deleteList.length == 0 && dir.isDirectory()){
                dir.delete();
            }
        }
    }

    private Point getAddressPoint(String address){
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/address.json")
                .queryParam("query", address)
                .queryParam("analyze_type", "similar")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        Point point = null;
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
            KakaoAddress kakaoAddress = objectMapper.convertValue(response.getBody(), KakaoAddress.class);
            if(kakaoAddress.getDocuments().size() > 0){
                Document doc = kakaoAddress.getDocuments().get(0);
                point = new Point(Double.parseDouble(doc.getX()), Double.parseDouble(doc.getY()));
            }
        }catch(Exception e){
            throw new KakaoMapErrorException();
        }

        return point;
    }
}
