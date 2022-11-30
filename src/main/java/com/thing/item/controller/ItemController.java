package com.thing.item.controller;

import com.thing.item.dto.APIResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.dto.ItemSearchRequestDTO;
import com.thing.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "")
    public APIResponseDTO showItemList(@RequestBody ItemSearchRequestDTO itemSearchRequestDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return APIResponseDTO.success(itemService.findItemList(itemSearchRequestDTO, auth.getName()));
    }

    @GetMapping(value = "/{item-id}")
    public APIResponseDTO showItemDetail(@PathVariable("item-id") Integer itemId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return APIResponseDTO.success(itemService.findItemOne(itemId, auth.getName()));
    }

    @GetMapping(value = "/clients/{client-idx}")
    public APIResponseDTO showUserItemList(@PathVariable("client-idx") Integer clientIdx, @RequestParam Integer page){
        return APIResponseDTO.success(itemService.findItemListByOwnerIndex(clientIdx, page));
    }

    @GetMapping(value = "/{item-id}/review")
    public APIResponseDTO showItemReview(@PathVariable("item-id") Integer itemId){
        return null;
    }

    @GetMapping(value = "/search")
    public APIResponseDTO searchItems(@RequestBody ItemSearchRequestDTO itemSearchRequestDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return APIResponseDTO.success(itemService.findItemList(itemSearchRequestDTO, auth.getName()));
    }

    @PostMapping(value = "")
    public APIResponseDTO createItem(@Valid @RequestBody ItemSaveRequestDTO itemSaveRequestDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemSaveRequestDTO.setOwnerId(Integer.parseInt(auth.getName()));
        itemService.saveItem(itemSaveRequestDTO);
        return APIResponseDTO.success();
    }

    @PutMapping(value = "/{item-id}")
    public APIResponseDTO modifyItem(
                                    @PathVariable("item-id") Integer itemId,
                                    @RequestPart(value = "item") ItemSaveRequestDTO itemSaveRequestDTO,
                                    @RequestPart(value = "itemPhoto") List<MultipartFile> itemPhotoSaveRequest) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemService.modifyItem(Integer.parseInt(auth.getName()), itemId, itemSaveRequestDTO, itemPhotoSaveRequest);
        return APIResponseDTO.success();
    }

    @DeleteMapping(value = "/{item-id}")
    public APIResponseDTO deleteItem(@PathVariable("item-id") Integer itemId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemService.deleteItem(itemId, Integer.parseInt(auth.getName()));
        return APIResponseDTO.success();
    }
}
