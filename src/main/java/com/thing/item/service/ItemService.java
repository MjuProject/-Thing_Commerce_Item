package com.thing.item.service;

import com.thing.item.domain.Item;
import com.thing.item.dto.ItemDetailResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.dto.ItemSearchRequestDTO;
import com.thing.item.dto.ItemSimpleResponseDTO;
import org.springframework.data.domain.Slice;

public interface ItemService {

    public Item saveItem(ItemSaveRequestDTO itemSaveRequestDTO);
    public ItemDetailResponseDTO findItemOne(Integer itemId, String clientIndex);
    public Slice<ItemSimpleResponseDTO> findItemList(ItemSearchRequestDTO itemSearchRequestDTO, String clientIndex);
    public Slice<ItemSimpleResponseDTO> findItemListByOwnerIndex(Integer clientIndex, int page);
    public void deleteItem(Integer itemId, Integer clientIndex);

}
