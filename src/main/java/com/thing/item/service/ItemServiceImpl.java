package com.thing.item.service;

import com.thing.item.client.KakaoMapClient;
import com.thing.item.domain.Item;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
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

    private Point getAddressPoint(String address){
        return new Point(0, 0);
    }
}
