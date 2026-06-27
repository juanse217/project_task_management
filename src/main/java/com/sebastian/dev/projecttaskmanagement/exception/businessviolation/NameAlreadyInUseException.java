package com.sebastian.dev.projecttaskmanagement.exception.businessviolation;

import com.sebastian.dev.projecttaskmanagement.exception.BusinessRuleViolationException;

public class NameAlreadyInUseException extends BusinessRuleViolationException {
    public NameAlreadyInUseException(String msg){
        super(msg);
    }
}
