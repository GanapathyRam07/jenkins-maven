package com.example.taskapi.common;
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
