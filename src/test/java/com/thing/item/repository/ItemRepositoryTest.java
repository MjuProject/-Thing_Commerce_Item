package com.thing.item.repository;

import com.thing.item.domain.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

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

}
