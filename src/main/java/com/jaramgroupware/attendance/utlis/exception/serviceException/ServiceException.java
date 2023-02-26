package com.jaramgroupware.attendance.utlis.exception.serviceException;

import lombok.Getter;


@Getter
public class ServiceException extends RuntimeException {
    private final ServiceErrorCode errorCode;

    public ServiceException(ServiceErrorCode errorCode){
        this.errorCode = errorCode;
    }
}