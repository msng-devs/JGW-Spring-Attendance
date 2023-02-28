package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.timeTable.TimeTableSpecification;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableSpecificationBuilder;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddRequestServiceDto;
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
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerErrorCode;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerException;
import com.jaramgroupware.attendance.utlis.validation.pageable.PageableValid;
import com.jaramgroupware.attendance.web.aop.authorization.Auth;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/timeTable")
@RestController
public class TimeTableApiController {

    private final AttendanceCodeService attendanceCodeService;
    private final TimeTableService timeTableService;
    private final AttendanceService attendanceService;
    private final CodeGenerator codeGenerator;
    private static final Integer CODE_LENGTH = 6;

    @Value("${jaramgroupware.attendance.targetRoles}")
    private Integer[] configTargetRoles;

    @Value("${jaramgroupware.attendance.targetRanks}")
    private Integer[] configTargetRanks;

    @Value("${jaramgroupware.attendance.defaultAttendanceType}")
    private Integer defaultAttendanceType;

    @Value("${jaramgroupware.attendance.registerAttendanceType}")
    private Integer registerAttendanceType;

    private List<Integer> targetRoles = null;
    private List<Integer> targetRanks = null;

    private final TimeTableSpecificationBuilder timeTableSpecificationBuilder;

    @PostConstruct
    private void setUp(){
        targetRoles =Arrays.asList(configTargetRoles.clone());
        targetRanks = Arrays.asList(configTargetRanks.clone());
    }

    @RBAC(role = 4)
    @PostMapping("/")
    public EntityModel<TimeTableResponseControllerDto> addTimeTable(
            @Valid @RequestBody TimeTableAddRequestControllerDto requestDto,
            @RequestParam(value = "addAttendance",defaultValue = "true") boolean isAddAttendance,
            @RequestHeader(value = "user_pk") String userUid){

        TimeTableResponseServiceDto newTimeTable;


        if(isAddAttendance){
            newTimeTable = timeTableService.createTimeTable(requestDto.toServiceDto(userUid), targetRoles, targetRanks,defaultAttendanceType);
        }else{
            newTimeTable = timeTableService.createTimeTable(requestDto.toServiceDto(userUid));
        }
        var data = newTimeTable.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(TimeTableApiController.class).findTimeTableById(data.getId())).withSelfRel(),
                linkTo(methodOn(EventApiController.class).findEventById(data.getEventID())).withRel("event"));
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
                linkTo(methodOn(TimeTableApiController.class).findTimeTableById(data.getId())).withSelfRel(),
                linkTo(methodOn(EventApiController.class).findEventById(data.getEventID())).withRel("event"));
    }

    @RBAC(role = 4)
    @DeleteMapping("/{timeTableId}")
    public ResponseEntity<MessageResponseDto> deleteTimeTable(
            @PathVariable Long timeTableId){

        var newTimeTable = timeTableService.deleteTimeTable(timeTableId);
        var data = newTimeTable.toControllerDto();

        return ResponseEntity.ok(new MessageResponseDto("OK"));
    }

    @Auth
    @GetMapping("/{timeTableId}")
    public EntityModel<TimeTableResponseControllerDto> findTimeTableById(
            @PathVariable Long timeTableId){

        var targetTimeTable = timeTableService.findServiceById(timeTableId);
        var data = targetTimeTable.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(TimeTableApiController.class).findTimeTableById(data.getId())).withSelfRel(),
                linkTo(methodOn(EventApiController.class).findEventById(data.getEventID())).withRel("event"));
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
                linkTo(methodOn(TimeTableApiController.class).getTimeTableAttendanceCode(timeTableId)).withSelfRel());
    }

    @RBAC(role = 2)
    @PostMapping("/{timeTableId}/attendanceCode/register")
    public ResponseEntity<AttendanceResponseControllerDto> registerAttendanceCode(
            @PathVariable Long timeTableId,
            @RequestParam(value = "code",defaultValue = "null") String code,
            @RequestHeader(value = "user_pk") String userUid)
    {
        var targetTimeTable = timeTableService.findServiceById(timeTableId);
        var codeInfo = attendanceCodeService.getCodeByTimeTable(timeTableId);

        if(!codeInfo.getCode().equals(code)){
            throw new ControllerException(ControllerErrorCode.ATTENDANCE_CODE_NOT_VALID);
        }

        var newAttendance = attendanceService.createAttendance(
                AttendanceAddRequestServiceDto.builder()
                        .attendanceTypeId(registerAttendanceType)
                        .createdBy("system")
                        .index("출결 코드를 통해 처리된 출결 정보 입니다.")
                        .memberId(userUid)
                        .timeTableId(timeTableId)
                        .build()
        );

        var data = newAttendance.toControllerDto();

        return ResponseEntity.ok(data);
    }

    @RBAC(role = 4)
    @GetMapping("/{timeTableId}/attendanceCode")
    public EntityModel<AttendanceCodeResponseControllerDto> getTimeTableAttendanceCode(@PathVariable Long timeTableId)
    {
        //해당 timetable이 존재하는지 검증
        timeTableService.findServiceById(timeTableId);

        var targetAttendanceCodeInfo = attendanceCodeService.getCodeByTimeTable(timeTableId);
        var data = AttendanceCodeResponseControllerDto.builder()
                .code(targetAttendanceCodeInfo.getCode())
                .expAt(targetAttendanceCodeInfo.getExpAt())
                .build();

        return EntityModel.of(data,
                linkTo(methodOn(TimeTableApiController.class).getTimeTableAttendanceCode(timeTableId)).withSelfRel());
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

    @Auth
    @GetMapping
    public ResponseEntity<List<TimeTableResponseControllerDto>> findTimeTableAll(
            @PageableDefault(page = 0,size = 20,sort = "id",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys = {"id","name","event","startDateTime","endDateTime","createdDateTime","modifiedDateTime","createBy","modifiedBy"},maxPageSize = 20) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam){

        //Specification 등록
        TimeTableSpecification spec = timeTableSpecificationBuilder.toSpec(queryParam);

        var results = timeTableService.findAll(spec,pageable)
                    .stream()
                    .map(TimeTableResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());

        return ResponseEntity.ok(results);

    }
}
