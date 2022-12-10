package com.thing.item.client;

import com.thing.item.dto.APIResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "basket-service")
public interface BasketServiceFeignClient {
    @GetMapping(value = "/baskets/items/{item-id}")
    APIResponseDTO<Integer> countBasket(@PathVariable("item-id") Integer itemId);

    @GetMapping(value = "/baskets/clients/{client-index}/items/{item-id}")
    APIResponseDTO<Boolean> showBasket(@PathVariable("client-index") Integer clientIndex, @PathVariable("item-id") Integer itemId);

    @GetMapping(value = "/baskets/clients/{client-index}")
    APIResponseDTO<List<Integer>> showBasketList(@PathVariable("client-index") Integer clientIndex);
}
