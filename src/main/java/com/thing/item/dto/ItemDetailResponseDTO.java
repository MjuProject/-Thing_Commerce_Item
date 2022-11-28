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
    private Long basketCount;
    private Boolean isLike;

    public static ItemDetailResponseDTO from(ClientInfoDTO clientInfoDTO, Item item, Long basketCount, Boolean isLike){
        return new ItemDetailResponseDTO(
                clientInfoDTO,
                item,
                basketCount,
                isLike
        );
    }
}
