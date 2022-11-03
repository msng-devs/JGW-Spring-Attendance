package com.jaramgroupware.attendance.web;

import com.jaramgroupware.attendance.domain.attendance.AttendanceID;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceIdResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeIdResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeRegisterRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableIdResponseControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceCodeService;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.service.AttendanceTypeService;
import com.jaramgroupware.attendance.service.TimeTableService;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import com.jaramgroupware.attendance.utils.key.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/attendance-code")
public class AttendanceCodeApiController {

    @Autowired
    private final TimeTableService timeTableService;

    @Autowired
    private final AttendanceCodeService attendanceCodeService;

    @Autowired
    private final AttendanceTypeService attendanceTypeService;

    @Autowired
    private final AttendanceService attendanceService;

    private final KeyGenerator keyGenerator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public ResponseEntity<AttendanceCodeIdResponseControllerDto> createdAttendanceCode(
            @RequestBody @Valid AttendanceCodeAddRequestControllerDto addRequestControllerDto,
            @RequestHeader("user_pk") String uid){

        //해당 ID의 Time table이 있는지 검증
        timeTableService.findById(addRequestControllerDto.getTimeTableId());

        //해당 TimeTable이 code를 가지고 있는지 검증
        if(attendanceCodeService.checkHasKey(addRequestControllerDto.getTimeTableId()))
            throw new CustomException(ErrorCode.ALREADY_HAS_CODE);

        //코드 생성
        //TODO 하드코딩 고치기
        String code = keyGenerator.getKey(6);

        attendanceCodeService.createCode(AttendanceCodeServiceDto.builder()
                .minute(addRequestControllerDto.getMinute())
                .code(code)
                .timeTableId(addRequestControllerDto.getTimeTableId())
                .build());

        return ResponseEntity.ok(new AttendanceCodeIdResponseControllerDto(code));
    }

    @DeleteMapping("/{timeTableId}")
    public ResponseEntity<TimeTableIdResponseControllerDto> revokeAttendanceCode(
            @PathVariable Long timeTableId,
            @RequestHeader("user_pk") String uid){

        //해당 키가 존재하는지 확인
        if(attendanceCodeService.validationKey(timeTableId)) throw new CustomException(ErrorCode.INVALID_TIMETABLE_ID);

        attendanceCodeService.revokeCode(timeTableId);

        return ResponseEntity.ok(new TimeTableIdResponseControllerDto(timeTableId));
    }

    @GetMapping("/{timeTableId}")
    public ResponseEntity<AttendanceCodeResponseControllerDto> findAttendanceCode(
            @PathVariable Long timeTableId,
            @RequestHeader("user_pk") String uid){

        AttendanceCodeResponseControllerDto result = attendanceCodeService.findKey(timeTableId).toControllerDto();

        return ResponseEntity.ok(result);
    }
    @PostMapping("/register")
    public ResponseEntity<AttendanceIdResponseControllerDto> registerAttendanceCode(
            @RequestBody @Valid AttendanceCodeRegisterRequestControllerDto attendanceCodeRegisterRequestControllerDto,
            @RequestHeader("user_pk") String uid){

        TimeTableResponseServiceDto targetTimeTable = timeTableService.findById(attendanceCodeRegisterRequestControllerDto.getTimeTableID());

        if(!attendanceCodeService.findKey
                (targetTimeTable.toEntity().getId()).getCode()
                .equals(attendanceCodeRegisterRequestControllerDto.getCode())) throw new CustomException(ErrorCode.ATTENDANCE_CODE_NOT_VALID);

        //검증이 완료되었다면, 해당 코드 등록
        //TODO 하드코딩 고치기
        AttendanceType registerType = attendanceTypeService.findById(1).toEntity();
        AttendanceID id = attendanceService.add(
                attendanceCodeRegisterRequestControllerDto.toAttendanceServiceDto(registerType,targetTimeTable.toEntity(), Member.builder().id(uid).build()),
                "system"
        );
        return ResponseEntity.ok(new AttendanceIdResponseControllerDto(id.getTimeTable().getId(),id.getMember().getId()));
    }
}
