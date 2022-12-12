package com.thing.item.advice;

import com.thing.item.dto.APIResponseDTO;
import com.thing.item.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ItemExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO unknown(Exception e){
        log.error("unknown exception", e);
        ItemExceptionType exceptionType = ItemExceptionType.UNKNOWN;
        return APIResponseDTO.fail(exceptionType.getCode(), exceptionType.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO clientNotFoundException(){
        ItemExceptionType exceptionType = ItemExceptionType.ITEM_NOT_FOUND;
        return APIResponseDTO.fail(exceptionType.getCode(), exceptionType.getMessage());
    }

    @ExceptionHandler(MisMatchOwnerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO mismatchOwnerException(){
        ItemExceptionType exceptionType = ItemExceptionType.MISMATCH_OWNER;
        return APIResponseDTO.fail(exceptionType.getCode(), exceptionType.getMessage());
    }

    @ExceptionHandler(ItemPhotoNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO itemPhotoNotFoundException(){
        ItemExceptionType exceptionType = ItemExceptionType.ITEM_PHOTO_NOT_FOUND;
        return APIResponseDTO.fail(exceptionType.getCode(), exceptionType.getMessage());
    }

    @ExceptionHandler(ItemPhotoSaveFailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO itemPhotoSaveFailException(){
        ItemExceptionType exceptionType = ItemExceptionType.ITEM_PHOTO_SAVE_FAIL;
        return APIResponseDTO.fail(exceptionType.getCode(), exceptionType.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected APIResponseDTO blankDataError(MethodArgumentNotValidException e){
        return APIResponseDTO.fail(-9002, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

}
