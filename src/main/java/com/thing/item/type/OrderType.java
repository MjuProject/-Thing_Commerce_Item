package com.thing.item.type;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.thing.item.domain.QItem.item;

@AllArgsConstructor
@Getter
public enum OrderType {
    ACCURATE(null),
    RECENTLY(item.createdDate.desc()),
    EXPENSIVE(item.price.desc()),
    INEXPENSIVE(item.price.asc()),
    DISTANCE(null);

    private OrderSpecifier order;
}
