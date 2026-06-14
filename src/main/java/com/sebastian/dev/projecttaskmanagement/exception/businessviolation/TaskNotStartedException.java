package com.sebastian.dev.projecttaskmanagement.exception.businessviolation;

import com.sebastian.dev.projecttaskmanagement.exception.BusinessRuleViolationException;

public class TaskNotStartedException extends BusinessRuleViolationException{
    public TaskNotStartedException(String msg){
        super(msg);
    }
}
