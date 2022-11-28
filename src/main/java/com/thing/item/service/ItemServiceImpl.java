package com.thing.item.service;

import com.thing.item.client.ClientServiceFeignClient;
import com.thing.item.client.KakaoMapClient;
import com.thing.item.domain.Item;
import com.thing.item.dto.ClientInfoDTO;
import com.thing.item.dto.ItemDetailResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.exception.ItemNotFoundException;
import com.thing.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final ClientServiceFeignClient clientServiceFeignClient;
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

    private Point getAddressPoint(String address){
        return new Point(0, 0);
    }
}
