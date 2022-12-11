package com.thing.item.exception;

public class KakaoMapErrorException extends RuntimeException{

    public KakaoMapErrorException(){
        super();
    }

    public KakaoMapErrorException(String message){
        super(message);
    }

    public KakaoMapErrorException(String message, Throwable th){
        super(message, th);
    }

}
