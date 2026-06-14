package com.sebastian.dev.projecttaskmanagement.exception;

public class BusinessRuleViolationException extends RuntimeException{
    public BusinessRuleViolationException(String msg){
        super(msg);
    }
}
