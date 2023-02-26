package com.jaramgroupware.attendance.utlis.exception.controllerExecption;

import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import lombok.Getter;


@Getter
public class ControllerException extends RuntimeException {
    private final ControllerErrorCode errorCode;

    public ControllerException(ControllerErrorCode errorCode){
        this.errorCode = errorCode;
    }
}