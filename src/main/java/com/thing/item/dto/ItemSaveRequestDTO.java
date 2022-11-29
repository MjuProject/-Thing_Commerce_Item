package com.thing.item.dto;

import com.thing.item.domain.Item;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItemSaveRequestDTO {

    private Integer categoryBig;
    private Integer categoryMiddle;
    private Integer categorySmall;
    @NotBlank(message = "물품의 제목이 입력되지 않았습니다.")
    private String itemTitle;
    @NotBlank(message = "물품의 내용이 입력되지 않았습니다.")
    private String itemContent;
    private int price;
    @NotBlank(message = "물품의 주소가 입력되지 않았습니다.")
    private String itemAddress;
    private double itemLatitude;
    private double itemLongitude;
    private Integer ownerId;

    public Item toEntity() {
        return Item.builder()
                .ownerId(ownerId)
                .itemTitle(itemTitle)
                .categoryBig(categoryBig)
                .categoryMiddle(categoryMiddle)
                .categorySmall(categorySmall)
                .itemContent(itemContent)
                .price(price)
                .itemAddress(itemAddress)
                .createdDate(LocalDate.now())
                .views(0)
                .status(true)
                .build();
    }

}


