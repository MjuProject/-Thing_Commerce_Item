package com.thing.item.repository;

import com.thing.item.domain.Item;
import com.thing.item.domain.ItemPhoto;
import com.thing.item.dto.ItemSimpleResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemPhotoRepository itemPhotoRepository;

    private Item item;

    @BeforeEach
    public void setUp(){
        item = Item.builder()
                .itemTitle("test")
                .itemContent("test")
                .itemAddress("test")
                .build();
    }

    @Test
    public void findById_test(){
        // given
        Item saveItem = itemRepository.save(item);
        Integer itemId = saveItem.getItemId();
        String title = saveItem.getItemTitle();

        // when
        Item findItem = itemRepository.findById(itemId).get();

        // then
        assertThat(itemId).isEqualTo(findItem.getItemId());
        assertThat(title).isEqualTo(findItem.getItemTitle());
    }

    @Test
    public void save_test(){
        // given
        String title = item.getItemTitle();

        // when
        Item saveItem = itemRepository.save(item);

        // then
        assertThat(title).isEqualTo(saveItem.getItemTitle());
    }

    @Test
    public void findByOwnerId_test(){
        // given
        Integer ownerId = 1;
        Item saveItem;
        for(int i = 1; i <= 15; i++){
            saveItem = itemRepository.save(Item.builder()
                    .ownerId(ownerId)
                    .itemTitle(String.valueOf(i))
                    .createdDate(LocalDate.now())
                    .build());

            itemPhotoRepository.save(ItemPhoto.builder()
                            .itemId(saveItem.getItemId())
                            .itemPhoto("test")
                            .isMain(true)
                            .build());
        }

        // when
        Slice<ItemSimpleResponseDTO> findList1 = itemRepository.findByOwnerId(ownerId, PageRequest.of(0, 10));
        Slice<ItemSimpleResponseDTO> findList2 = itemRepository.findByOwnerId(ownerId, PageRequest.of(1, 10));

        // then
        assertThat(findList1.getContent().size()).isEqualTo(10);
        assertThat(findList2.getContent().size()).isEqualTo(5);
    }

}
