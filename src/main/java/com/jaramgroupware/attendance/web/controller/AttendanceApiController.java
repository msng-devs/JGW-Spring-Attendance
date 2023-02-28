package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecificationBuilder;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.others.controllerDto.MessageResponseDto;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerErrorCode;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerException;
import com.jaramgroupware.attendance.utlis.validation.pageable.PageableValid;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attendance")
@RestController
public class AttendanceApiController {
    //TODO hateoas 추가하기
    private final AttendanceService attendanceService;
    private final AttendanceSpecificationBuilder attendanceSpecificationBuilder;

    @RBAC(role = 4)
    @PostMapping("/")
    public ResponseEntity<AttendanceResponseControllerDto> createAttendance(
            @RequestBody @Valid AttendanceAddRequestControllerDto requestDto,
            @RequestHeader("user_pk") String userUid)
    {
        var newAttendance = attendanceService.createAttendance(requestDto.toServiceDto(userUid));
        var data = newAttendance.toControllerDto();

        return ResponseEntity.ok(data);
    }

    @RBAC(role = 4)
    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponseControllerDto> updateAttendance(
            @RequestBody @Valid AttendanceUpdateRequestControllerDto requestDto,
            @RequestHeader("user_pk") String userUid,
            @PathVariable Long attendanceId)
    {
        var newAttendance = attendanceService.updateAttendance(requestDto.toServiceDto(userUid,attendanceId));
        var data = newAttendance.toControllerDto();
        return ResponseEntity.ok(data);
    }

    @RBAC(role = 2)
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponseControllerDto> findAttendanceById(
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
        return ResponseEntity.ok(data);
    }

    @RBAC(role = 4)
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<MessageResponseDto> deleteAttendance(@PathVariable Long attendanceId){
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok(new MessageResponseDto("OK"));
    }

    @RBAC(role = 2)
    @GetMapping
    public ResponseEntity<List<AttendanceResponseControllerDto>> getAttendanceAll(
            @PageableDefault(page = 0,size = 20,sort = "id",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys = {"id","attendanceType","member","timeTable","index","createdDateTime","modifiedDateTime","createBy","modifiedBy"},maxPageSize = 200) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam,
            @RequestHeader("user_pk") String uid,
            @RequestHeader("role_pk") Integer roleID){

        //만약 자신이 아닌 데이터를 검색할려고 하면 권한 인증
        if((!queryParam.containsKey("memberID") || !Objects.equals(queryParam.getFirst("memberID"), uid)) && roleID < 4){
            throw new ControllerException(ControllerErrorCode.FORBIDDEN_ROLE);
        }

        //Specification 등록
        AttendanceSpecification spec = attendanceSpecificationBuilder.toSpec(queryParam);
        var results = attendanceService.findAll(spec,pageable)
                    .stream()
                    .map(AttendanceResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

}
