package com.sebastian.dev.projecttaskmanagement.exception.businessviolation;

import com.sebastian.dev.projecttaskmanagement.exception.BusinessRuleViolationException;

public class ProjectAlreadyExistsException extends BusinessRuleViolationException {
    public ProjectAlreadyExistsException(String msg){
        super(msg);
    }
}
