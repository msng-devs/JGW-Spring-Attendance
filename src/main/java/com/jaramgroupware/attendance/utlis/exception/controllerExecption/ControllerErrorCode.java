package com.jaramgroupware.attendance.utlis.exception.controllerExecption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ControllerErrorCode {

    ALREADY_HAS_CODE("AT_008","이미 attendance code가 발급된 timetable 입니다.",HttpStatus.BAD_REQUEST,"timetable은 단 한개의 attendance code를 가질 수 있습니다. 해당 timetable은 이미 attendance code를 가지고 있으며, 해당 attendance code를 취소하고 다시 시도하시거나, attendance code가 만료된 후 다시 시도해주세요 "),
    FORBIDDEN_ROLE("AT_009","접근 가능한 role이 아닙니다.",HttpStatus.FORBIDDEN,"해당 API를 사용하기에 부족한 role level입니다. 해당 API를 사용하기에 적합한 role을 가진 계정인지 다시 확인해주세요.");


    private final String type;
    private final String title;
    private final HttpStatus httpStatus;
    private final String detail;

}
