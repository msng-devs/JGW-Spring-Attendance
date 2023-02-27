package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.others.controllerDto.MessageResponseDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableResponseControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceCodeService;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.service.TimeTableService;
import com.jaramgroupware.attendance.utlis.code.CodeGenerator;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/timeTable")
@RestController
public class TimeTableApiController {

    private final AttendanceCodeService attendanceCodeService;
    private final TimeTableService timeTableService;
    private final CodeGenerator codeGenerator;
    private static final Integer CODE_LENGTH = 6;

    @Value("${jaramgroupware.attendance.targetRoles}")
    private Integer[] targetRoles;

    @Value("${jaramgroupware.attendance.targetRanks}")
    private Integer[] targetRanks;

    @Value("${jaramgroupware.attendance.defaultAttendanceType}")
    private Integer defaultAttendanceType;

    @RBAC(role = 4)
    @PostMapping("/")
    public EntityModel<TimeTableResponseControllerDto> addTimeTable(
            @Valid @RequestBody TimeTableAddRequestControllerDto requestDto,
            @RequestParam(value = "addAttendance",defaultValue = "true") boolean isAddAttendance,
            @RequestHeader(value = "user_pk") String userUid){
        TimeTableResponseServiceDto newTimeTable;


        if(isAddAttendance){
            newTimeTable = timeTableService.createTimeTable(requestDto.toServiceDto(userUid),targetRoles,targetRanks,defaultAttendanceType);
        }else{
            newTimeTable = timeTableService.createTimeTable(requestDto.toServiceDto(userUid));
        }
        var data = newTimeTable.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(TimeTableApiController.class).findTimeTableById(timeTableId)).withSelfRel() x

                )
    }

    @RBAC(role = 4)
    @PutMapping("/{timeTableId}")
    public EntityModel<TimeTableResponseControllerDto> updateTimeTable(
            @PathVariable Long timeTableId,
            @Valid @RequestBody TimeTableUpdateRequestControllerDto requestDto,
            @RequestHeader(value = "user_pk") String userUid){

        var newTimeTable = timeTableService.updateTimeTable(requestDto.toServiceDto(timeTableId,userUid));
        var data = newTimeTable.toControllerDto();

        return EntityModel.of(data,

                )
    }

    @RBAC(role = 4)
    @DeleteMapping("/{timeTableId}")
    public EntityModel<TimeTableResponseControllerDto> deleteTimeTable(
            @PathVariable Long timeTableId){

        var newTimeTable = timeTableService.deleteTimeTable(timeTableId);
        var data = newTimeTable.toControllerDto();

        return EntityModel.of(data,

                )
    }

    @GetMapping("/{timeTableId}")
    public EntityModel<TimeTableResponseControllerDto> findTimeTableById(
            @PathVariable Long timeTableId){

        var targetTimeTable = timeTableService.findServiceById(timeTableId);
        var data = targetTimeTable.toControllerDto();

        return EntityModel.of(data,

                )
    }

    @RBAC(role = 4)
    @PostMapping("/{timeTableId}/attendanceCode")
    public EntityModel<AttendanceCodeResponseControllerDto> publishTimeTableAttendanceCode(
            @PathVariable Long timeTableId,
            @Valid @RequestBody AttendanceCodeAddRequestControllerDto requestDto)
    {
        //해당 timetable이 존재하는지 검증
        timeTableService.findServiceById(timeTableId);

        var newAttendanceCodeInfo = attendanceCodeService.createCode(
                AttendanceCodeAddRequestServiceDto.builder()
                        .code(codeGenerator.getKey(CODE_LENGTH))
                        .timeTableId(timeTableId.toString())
                        .expSec(requestDto.getExpSec())
                        .build());

        var data = AttendanceCodeResponseControllerDto.builder()
                .code(newAttendanceCodeInfo.getCode())
                .expAt(newAttendanceCodeInfo.getExpAt())
                .build();

        return EntityModel.of(data,
                linkTo(methodOn(TimeTableApiController.class).getTimeTableAttendanceCode(timeTableId)).withSelfRel())
    }

    @RBAC(role = 4)
    @GetMapping("/{timeTableId}/attendanceCode")
    public ResponseEntity<AttendanceCodeResponseControllerDto> getTimeTableAttendanceCode(@PathVariable Long timeTableId)
    {
        //해당 timetable이 존재하는지 검증
        timeTableService.findServiceById(timeTableId);

        var targetAttendanceCodeInfo = attendanceCodeService.getCodeByTimeTable(timeTableId);

        return ResponseEntity.ok(AttendanceCodeResponseControllerDto.builder()
                .code(targetAttendanceCodeInfo.getCode())
                .expAt(targetAttendanceCodeInfo.getExpAt())
                .build());
    }

    @RBAC(role = 4)
    @DeleteMapping("/{timeTableId}/attendanceCode")
    public ResponseEntity<MessageResponseDto> deleteTimeTableAttendanceCode(@PathVariable Long timeTableId)
    {
        //해당 timetable이 존재하는지 검증
        timeTableService.findServiceById(timeTableId);
        attendanceCodeService.revokeCode(timeTableId);

        return ResponseEntity.ok(new MessageResponseDto("OK"));
    }
}
