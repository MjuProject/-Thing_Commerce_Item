package com.thing.item.dto;

import com.thing.item.type.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ItemSearchRequestDTO {

    private Integer categoryBig;
    private Integer categoryMiddle;
    private Integer categorySmall;
    private String query;
    private Double longitude;
    private Double latitude;
    private Integer page;
    private OrderType orderType;

}
