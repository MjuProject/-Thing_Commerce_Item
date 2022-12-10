package com.thing.item.dto;


import com.thing.item.domain.Item;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemDetailResponseDTO {

    private ClientInfoDTO ownerInfo;
    private Item item;
    private Integer basketCount;
    private Boolean isLike;

    public static ItemDetailResponseDTO from(ClientInfoDTO clientInfoDTO, Item item, Integer basketCount, Boolean isLike){
        return new ItemDetailResponseDTO(
                clientInfoDTO,
                item,
                basketCount,
                isLike
        );
    }
}
