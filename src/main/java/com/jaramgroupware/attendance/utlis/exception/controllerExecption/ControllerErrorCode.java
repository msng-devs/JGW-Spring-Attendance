package com.jaramgroupware.attendance.utlis.exception.controllerExecption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ControllerErrorCode {

    FORBIDDEN_ROLE("AT_009","접근 가능한 role이 아닙니다.",HttpStatus.FORBIDDEN,"해당 API를 사용하기에 부족한 role level입니다. 해당 API를 사용하기에 적합한 role을 가진 계정인지 다시 확인해주세요.");


    private final String type;
    private final String title;
    private final HttpStatus httpStatus;
    private final String detail;

}
