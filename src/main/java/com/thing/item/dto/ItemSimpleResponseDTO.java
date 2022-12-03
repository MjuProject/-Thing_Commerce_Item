package com.thing.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemSimpleResponseDTO {

    private Integer itemId;
    private String itemTitle;
    private String itemAddress;
    private Integer price;
    private String itemPhoto;
    private Boolean status;
    private Date createdDate;
    private Boolean isLike;

    @QueryProjection
    public ItemSimpleResponseDTO(Integer itemId, String itemTitle, String itemAddress, Integer price, String itemPhoto, Boolean status, Date createdDate) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemAddress = itemAddress;
        this.price = price;
        this.itemPhoto = itemPhoto;
        this.status = status;
        this.createdDate = createdDate;
    }
}
