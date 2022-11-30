package com.thing.item.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemExceptionType {

    UNKNOWN(-9999, "알 수 없는 오류가 발생하였습니다."),
    ITEM_NOT_FOUND(-3000, "해당 물품이 존재하지 않습니다."),
    MISMATCH_OWNER(-3001, "해당 물품의 주인이 아닙니다.");

    private final int code;
    private final String message;
}
