package com.example.demo.model;

import lombok.Data;

@Data
public class ResponseBodyMessage<T> {
    private int status;
    private String message;
    private T data;

    public ResponseBodyMessage(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
