package com.thing.item.controller;

import com.thing.item.domain.ItemPhoto;
import com.thing.item.dto.APIResponseDTO;
import com.thing.item.dto.ItemSaveRequestDTO;
import com.thing.item.dto.ItemSearchRequestDTO;
import com.thing.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "")
    public APIResponseDTO showItemList(ItemSearchRequestDTO itemSearchRequestDTO){
        return APIResponseDTO.success(itemService.findItemList(itemSearchRequestDTO, getClientIndex()));
    }

    @GetMapping(value = "/{item-id}")
    public APIResponseDTO showItemDetail(@PathVariable("item-id") Integer itemId){
        return APIResponseDTO.success(itemService.findItemOne(itemId, getClientIndex()));
    }

    @GetMapping(value = "/clients/{client-idx}")
    public APIResponseDTO showUserItemList(@PathVariable("client-idx") Integer clientIdx, @RequestParam Integer page){
        return APIResponseDTO.success(itemService.findItemListByOwnerIndex(clientIdx, page));
    }

    @GetMapping(value = "/search")
    public APIResponseDTO searchItems(ItemSearchRequestDTO itemSearchRequestDTO){
        return APIResponseDTO.success(itemService.findItemList(itemSearchRequestDTO, getClientIndex()));
    }

    @PostMapping(value = "")
    public APIResponseDTO createItem(@Valid @RequestPart(value = "item") ItemSaveRequestDTO itemSaveRequestDTO,
                                     @RequestPart(value = "itemPhoto") List<MultipartFile> itemPhotoSaveRequest){
        itemSaveRequestDTO.setOwnerId(getClientIndex());
        itemService.saveItem(itemSaveRequestDTO, itemPhotoSaveRequest);
        return APIResponseDTO.success();
    }

    @PutMapping(value = "/{item-id}")
    public APIResponseDTO modifyItem(
                                    @PathVariable("item-id") Integer itemId,
                                    @Valid @RequestPart(value = "item") ItemSaveRequestDTO itemSaveRequestDTO,
                                    @RequestPart(value = "itemPhoto") List<MultipartFile> itemPhotoSaveRequest) throws IOException {
        itemService.modifyItem(getClientIndex(), itemId, itemSaveRequestDTO, itemPhotoSaveRequest);
        return APIResponseDTO.success();
    }

    @DeleteMapping(value = "/{item-id}")
    public APIResponseDTO deleteItem(@PathVariable("item-id") Integer itemId){
        itemService.deleteItem(itemId, getClientIndex());
        return APIResponseDTO.success();
    }

    @GetMapping(value = "/{item-id}/item-photos/{item-photo-index}")
    public byte[] showPhoto(@PathVariable("item-id") Integer itemId, @PathVariable("item-photo-index") Integer itemPhotoIndex) throws IOException {
        InputStream imageStream = new FileInputStream(itemService.getItemPhotoPath(itemPhotoIndex));
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return imageByteArray;
    }

    private Integer getClientIndex(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String clientIndex = auth.getName();
        return clientIndex.equals("anonymousUser")? -1 : Integer.parseInt(clientIndex);
    }
}
