package com.thing.item.service;

import com.thing.item.client.ClientServiceFeignClient;
import com.thing.item.domain.Item;
import com.thing.item.dto.APIResponseDTO;
import com.thing.item.dto.ClientInfoDTO;
import com.thing.item.dto.ItemDetailResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @InjectMocks
    private ClientServiceFeignClient clientServiceFeignClient = mock(ClientServiceFeignClient.class);

    private Item item;

    @BeforeEach
    public void setUp(){
        item = Item.builder()
                .itemId(1)
                .itemTitle("test")
                .ownerId(1)
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

    @Test
    public void findItemOne_test(){
        // given
        ClientInfoDTO clientInfoDTO = ClientInfoDTO
                .builder()
                .clientIndex(1)
                .build();

        Integer itemId = item.getItemId();
        Integer ownerIndex = clientInfoDTO.getClientIndex();
        Integer userIndex = 2;

        given(itemRepository.findById(any())).willReturn(Optional.ofNullable(item));
        given(clientServiceFeignClient.getClient(any())).willReturn(APIResponseDTO.success(clientInfoDTO));

        // when
        ItemDetailResponseDTO itemDetailResponseDTO = itemService.findItemOne(itemId, String.valueOf(userIndex));

        // then
        assertThat(itemId).isEqualTo(itemDetailResponseDTO.getItem().getItemId());
        assertThat(ownerIndex).isEqualTo(itemDetailResponseDTO.getOwnerInfo().getClientIndex());
    }

}
