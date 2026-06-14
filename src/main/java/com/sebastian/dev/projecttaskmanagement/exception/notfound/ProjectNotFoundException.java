package com.sebastian.dev.projecttaskmanagement.exception.notfound;

import com.sebastian.dev.projecttaskmanagement.exception.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException{
    public ProjectNotFoundException(String msg){
        super(msg);
    }
}
