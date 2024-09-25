package com.FoodApp.FoodOrderingApp.customException;

import java.util.HashMap;

public class CustomException extends Exception{

    private Error errorCode;
    private String message;
    private Exception e;
    private HashMap<String, Object> metadata;

    public CustomException(Error errorCode, String message){
        super();
        this.errorCode = errorCode;
        this.message = message;
    }

    public CustomException( String message){
        super();
        this.message = message;
    }

    public CustomException(String s, Exception e) {
        super();
        this.message = s;
        this.e = e;
    }
}
