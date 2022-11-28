package com.thing.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponseDTO<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    public APIResponseDTO(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static APIResponseDTO success(){
        return new APIResponseDTO(true, 0, "성공하였습니다.");
    }

    public static <T> APIResponseDTO success(T data){
        return new APIResponseDTO(true, 0, "성공하였습니다.", data);
    }

    public static APIResponseDTO fail(int code, String message){
        return new APIResponseDTO(false, code, message);
    }
}
