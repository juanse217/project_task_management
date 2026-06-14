package com.sebastian.dev.projecttaskmanagement.exception.notfound;

import com.sebastian.dev.projecttaskmanagement.exception.ResourceNotFoundException;

public class TaskNotFoundException extends ResourceNotFoundException{
    public TaskNotFoundException(String msg){
        super(msg);
    }
}
