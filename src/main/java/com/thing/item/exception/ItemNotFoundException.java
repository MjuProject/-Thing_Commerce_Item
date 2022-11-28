package com.thing.item.exception;

public class ItemNotFoundException extends RuntimeException{

    public ItemNotFoundException(){
        super();
    }

    public ItemNotFoundException(String message){
        super(message);
    }

    public ItemNotFoundException(String message, Throwable th){
        super(message, th);
    }

}
