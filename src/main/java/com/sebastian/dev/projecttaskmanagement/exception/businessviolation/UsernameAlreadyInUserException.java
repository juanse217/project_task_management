package com.sebastian.dev.projecttaskmanagement.exception.businessviolation;

public class UsernameAlreadyInUserException extends RuntimeException{
    public UsernameAlreadyInUserException(String msg){
        super(msg);
    }
}
