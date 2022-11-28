package com.thing.item.service;

import com.thing.item.domain.Item;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;

    @BeforeEach
    public void setUp(){
        item = Item.builder()
                .itemId(1)
                .itemTitle("test")
                .build();
    }

    @Test
    public void saveItem_test(){
        // given
        ItemSaveRequestDTO itemSaveRequestDTO = ItemSaveRequestDTO.builder()
                .itemTitle(item.getItemTitle())
                .build();
        Integer itemId = item.getItemId();
        String title = itemSaveRequestDTO.getItemTitle();

        given(itemRepository.save(any())).willReturn(item);

        // when
        Item saveItem = itemService.saveItem(itemSaveRequestDTO);

        // then
        assertThat(itemId).isEqualTo(saveItem.getItemId());
        assertThat(title).isEqualTo(saveItem.getItemTitle());
    }

}
