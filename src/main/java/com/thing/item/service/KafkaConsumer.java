package com.thing.item.service;

import com.thing.item.domain.Item;
import com.thing.item.exception.ItemNotFoundException;
import com.thing.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ItemRepository itemRepository;

    @KafkaListener(topics = "itemSold_topic")
    public void processMessage(String kafkaMessage){
        log.info("kafka Message: =====> " + kafkaMessage);
        int itemId = Integer.parseInt(kafkaMessage);
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.sold();
    }

}
