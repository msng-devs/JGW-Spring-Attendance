package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.others.controllerDto.MessageResponseDto;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerErrorCode;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerException;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attendance")
@RestController
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    @RBAC(role = 4)
    @PostMapping("/")
    public EntityModel<AttendanceResponseControllerDto> createAttendance(
            @RequestBody @Valid AttendanceAddRequestControllerDto requestDto,
            @RequestHeader("user_pk") String userUid)
    {
        var newAttendance = attendanceService.createAttendance(requestDto.toServiceDto(userUid));
        var data = newAttendance.toControllerDto();
    }

    @RBAC(role = 4)
    @PutMapping("/{attendanceId}")
    public EntityModel<AttendanceResponseControllerDto> updateAttendance(
            @RequestBody @Valid AttendanceUpdateRequestControllerDto requestDto,
            @RequestHeader("user_pk") String userUid,
            @PathVariable Long attendanceId)
    {
        var newAttendance = attendanceService.updateAttendance(requestDto.toServiceDto(userUid,attendanceId));
        var data = newAttendance.toControllerDto();
    }

    @RBAC(role = 2)
    @GetMapping("/{attendanceId}")
    public EntityModel<AttendanceResponseControllerDto> findAttendanceById(
            @PathVariable Long attendanceId,
            @RequestHeader("user_pk") String userUid,
            @RequestHeader("role_pk") Integer roleId)
    {
        var targetAttendance = attendanceService.findAttendanceById(attendanceId);

        //Admin이 아닌 유저가 타인의 attendance 정보에 접근하면,
        if(roleId < 4 && !targetAttendance.getMemberID().equals(userUid)){
            throw new ControllerException(ControllerErrorCode.FORBIDDEN_ROLE);
        }

        var data = targetAttendance.toControllerDto();
    }

    @RBAC(role = 4)
    @DeleteMapping("/{attendanceId}")
    public EntityModel<MessageResponseDto> deleteAttendance(@PathVariable Long attendanceId){
        attendanceService.deleteAttendance(attendanceId);
    }


}
