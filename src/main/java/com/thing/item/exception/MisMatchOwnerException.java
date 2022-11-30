package com.thing.item.exception;

public class MisMatchOwnerException extends RuntimeException{

    public MisMatchOwnerException(){
        super();
    }

    public MisMatchOwnerException(String message){
        super(message);
    }

    public MisMatchOwnerException(String message, Throwable th){
        super(message, th);
    }

}
