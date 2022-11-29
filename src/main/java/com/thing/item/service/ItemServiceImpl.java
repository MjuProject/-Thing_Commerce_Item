package com.thing.item.service;

import com.thing.item.client.ClientServiceFeignClient;
import com.thing.item.client.KakaoMapClient;
import com.thing.item.domain.ElasticItem;
import com.thing.item.domain.Item;
import com.thing.item.dto.*;
import com.thing.item.exception.ItemNotFoundException;
import com.thing.item.repository.ElasticItemRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final ClientServiceFeignClient clientServiceFeignClient;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final ElasticItemRepository elasticItemRepository;
//    private final KakaoMapClient kakaoMapClient;

    @Transactional
    @Override
    public Item saveItem(ItemSaveRequestDTO itemSaveRequestDTO) {
        Item item = itemSaveRequestDTO.toEntity();
        Point addressPoint = getAddressPoint(item.getItemAddress());
        item.setPoint(addressPoint);
        item = itemRepository.save(item);
        // 사진 저장
        return item;
    }

    @Override
    public ItemDetailResponseDTO findItemOne(Integer itemId, String clientIndex) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.addView();
        ClientInfoDTO clientInfoDTO = clientServiceFeignClient.getClient(item.getOwnerId()).getData();
        // 장바구니 찜 갯수 구하기
        Long basketCount = 0L;
        // 해당 물건 찜 유무 확인
        boolean isLike = false;
        return ItemDetailResponseDTO.from(clientInfoDTO, item, basketCount, isLike);
    }

    @Override
    public Slice<ItemSimpleResponseDTO> findItemList(ItemSearchRequestDTO itemSearchRequestDTO, String clientIndex) {
        Pageable pageable = PageRequest.of(itemSearchRequestDTO.getPage(), 10);
        List<ElasticItem> itemList = null;
        if (StringUtils.hasText(itemSearchRequestDTO.getQuery())){
            itemList = elasticItemRepository.searchItemByQuery(itemSearchRequestDTO.getQuery());
        }
        List<ItemSimpleResponseDTO> content = itemRepositoryCustom.findByItemList(itemSearchRequestDTO, pageable, itemList);
        // 장바구니 처리

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Point getAddressPoint(String address){
        return new Point(0, 0);
    }
}
