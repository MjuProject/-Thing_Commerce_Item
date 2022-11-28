package com.thing.item.service;

import com.thing.item.domain.Item;
import com.thing.item.dto.ItemDetailResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;

public interface ItemService {

    public Item saveItem(ItemSaveRequestDTO itemSaveRequestDTO);
    public ItemDetailResponseDTO findItemOne(Integer itemId, String clientIndex);

}
