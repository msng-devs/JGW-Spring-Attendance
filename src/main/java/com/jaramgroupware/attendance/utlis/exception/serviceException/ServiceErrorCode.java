package com.jaramgroupware.attendance.utlis.exception.serviceException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceErrorCode {


    INVALID_TIMETABLE("AT_001","알 수 없는 timetable 입니다.",HttpStatus.NOT_FOUND,"해당 timetable을 찾을 수 없습니다. 해당 timetable의 정보를 다시 확인하세요"),
    INVALID_EVENT("AT_002","알 수 없는 event 입니다.",HttpStatus.NOT_FOUND,"해당 event를 찾을 수 없습니다. 해당 event의 정보를 다시 확인하세요"),
    INVALID_ATTENDANCE_CODE("AT_003","알 수 없는 attendance code 입니다.",HttpStatus.NOT_FOUND,"해당 attendance code를 찾을 수 없습니다. attendance code를 다시 확인하세요"),
    INVALID_ATTENDANCE("AT_004","알 수 없는 attendance 입니다.",HttpStatus.NOT_FOUND,"해당 attendance를 찾을 수 없습니다. attendance를 다시 확인하세요"),
    INVALID_ATTENDANCE_TYPE("AT_005","알 수 없는 attendance type 입니다.",HttpStatus.NOT_FOUND,"해당 attendance type을 찾을 수 없습니다. attendance type을 다시 확인하세요"),
    INVALID_MEMBER("AT_006","알 수 없는 member 입니다.",HttpStatus.NOT_FOUND,"해당 member를 찾을 수 없습니다. member를 다시 확인하세요"),
    INVALID_TIMETABLE_DATETIME("AT_010","timetable의 시간이 잘못되었습니다.",HttpStatus.BAD_REQUEST,"timetable의 시작 시간 및 종료 시간은 해당 event의 시작 시간 및 종료 시간 안으로 설정해야 합니다. 시간 설정을 다시 확인해주세요"),

    ALREADY_HAS_CODE("AT_008","이미 attendance code가 발급된 timetable 입니다.",HttpStatus.BAD_REQUEST,"timetable은 단 한개의 attendance code를 가질 수 있습니다. 해당 timetable은 이미 attendance code를 가지고 있으며, 해당 attendance code를 취소하고 다시 시도하시거나, attendance code가 만료된 후 다시 시도해주세요 "),
    CANNOT_CHANGE_EVENT_DATETIMES("AT_011","해당 event의 시간을 변경할 수 없습니다.",HttpStatus.BAD_REQUEST,"변경할려는 event 아래 timetable들의 기간이 변경 후 event의 기간을 벗어납니다. timetable을 삭제 및 수정 후 다시 시도하세요.");


    private final String type;
    private final String title;
    private final HttpStatus httpStatus;
    private final String detail;

}
