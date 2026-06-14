package com.sebastian.dev.projecttaskmanagement.exception.businessviolation;

import com.sebastian.dev.projecttaskmanagement.exception.BusinessRuleViolationException;

public class ProjectAlreadyContainsTaskException extends BusinessRuleViolationException{
    public ProjectAlreadyContainsTaskException(String msg){
        super(msg);
    }
}
