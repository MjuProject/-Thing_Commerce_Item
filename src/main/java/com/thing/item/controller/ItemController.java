package com.thing.item.controller;

import com.thing.item.dto.APIResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "")
    public APIResponseDTO<Object> showItemList(){
        return null;
    }

    @GetMapping(value = "/me")
    public APIResponseDTO<Object> showMyList(){
        return null;
    }

    @GetMapping(value = "/{item-id}")
    public APIResponseDTO<Object> showItemDetail(@PathVariable("item-id") Integer itemId){
        return null;
    }

    @GetMapping(value = "/clients/{client-idx}")
    public APIResponseDTO<Object> showUserItemList(@PathVariable("client-idx") Integer clientIdx){
        return null;
    }

    @GetMapping(value = "/{item-id}/review")
    public APIResponseDTO<Object> showItemReview(@PathVariable("item-id") Integer itemId){
        return null;
    }

    @GetMapping(value = "/search")
    public APIResponseDTO<Object> searchItems(){
        return null;
    }

    @PostMapping(value = "")
    public APIResponseDTO createItem(@Valid @RequestBody ItemSaveRequestDTO itemSaveRequestDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemSaveRequestDTO.setOwnerId(Integer.parseInt(auth.getName()));
        itemService.saveItem(itemSaveRequestDTO);
        return APIResponseDTO.success();
    }

}
