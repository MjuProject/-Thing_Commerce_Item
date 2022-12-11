package com.thing.item.service;

import com.thing.item.client.BasketServiceFeignClient;
import com.thing.item.client.ClientServiceFeignClient;
import com.thing.item.client.KakaoMapClient;
import com.thing.item.domain.ElasticItem;
import com.thing.item.domain.Item;
import com.thing.item.dto.*;
import com.thing.item.exception.ItemNotFoundException;
import com.thing.item.exception.MisMatchOwnerException;
import com.thing.item.repository.ElasticItemRepository;
import com.thing.item.repository.ItemPhotoRepository;
import com.thing.item.repository.ItemRepository;
import com.thing.item.repository.ItemRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final ItemPhotoRepository itemPhotoRepository;
    private final ClientServiceFeignClient clientServiceFeignClient;
    private final BasketServiceFeignClient basketServiceFeignClient;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final ElasticItemRepository elasticItemRepository;
//    private final KakaoMapClient kakaoMapClient;

    @Transactional
    @Override
    public Item saveItem(ItemSaveRequestDTO itemSaveRequestDTO, List<MultipartFile> itemPhotoSaveRequest) {
        Item item = itemSaveRequestDTO.toEntity();
        Point addressPoint = getAddressPoint(item.getItemAddress());
        item.setPoint(addressPoint);
        item = itemRepository.save(item);
        // 사진 저장
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

        itemRepository.delete(item);
    }

    @Transactional
    @Override
    public void modifyItem(Integer clientIndex, Integer itemId, ItemSaveRequestDTO itemSaveRequestDTO, List<MultipartFile> itemPhotoSaveRequest) throws IOException {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if(!item.getOwnerId().equals(clientIndex))
            throw new MisMatchOwnerException();

        // 사진 파일 삭제 로직

        Point point = getAddressPoint(itemSaveRequestDTO.getItemAddress());
        itemSaveRequestDTO.setItemLongitude(point.getX());
        itemSaveRequestDTO.setItemLatitude(point.getY());

        item.modifyItemInfo(itemSaveRequestDTO);
        itemRepository.save(item);
        
        itemPhotoRepository.deleteAll(item.getPhotos());
        // 사진 저장 로직
    }

    private void savePhotos(){

    }

    private void deletePhotos(){

    }

    private Point getAddressPoint(String address){
        return new Point(0, 0);
    }
}
