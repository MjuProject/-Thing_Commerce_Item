package com.thing.item.dto;

import com.thing.item.domain.Item;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ItemDTO {

    private Integer itemId;
    private Integer categoryBig;
    private Integer categoryMiddle;
    private Integer categorySmall;
    private String itemTitle;
    private String itemContent;
    private int price;
    private String itemAddress;
    private int views;
    private Date createdDate;
    private boolean status;
    private List<String> itemPhotoUri;

    public static ItemDTO from(Item item){
        List<String> itemPhotoUri = item.getPhotos().stream()
                .map(itemPhoto -> "/items/" + itemPhoto.getItemId() + "/item-photos/" + itemPhoto.getItemPhotoIndex())
                .collect(Collectors.toList());

        return ItemDTO.builder()
                .itemId(item.getItemId())
                .categoryBig(item.getCategoryBig())
                .categoryMiddle(item.getCategoryMiddle())
                .categorySmall(item.getCategorySmall())
                .itemTitle(item.getItemTitle())
                .itemContent(item.getItemContent())
                .price(item.getPrice())
                .itemAddress(item.getItemAddress())
                .views(item.getViews())
                .createdDate(item.getCreatedDate())
                .status(item.isStatus())
                .itemPhotoUri(itemPhotoUri)
                .build();
    }

}
