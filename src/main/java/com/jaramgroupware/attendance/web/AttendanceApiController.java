package com.jaramgroupware.attendance.web;

import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecificationBuilder;
import com.jaramgroupware.attendance.domain.role.Role;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.*;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.general.controllerDto.MessageDto;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import com.jaramgroupware.attendance.utils.validation.PageableValid;
import com.jaramgroupware.attendance.utils.validation.attendance.BulkAddAttendanceValid;
import com.jaramgroupware.attendance.utils.validation.attendance.BulkUpdateAttendanceValid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/attendance")
public class AttendanceApiController {

    private final AttendanceService attendanceService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AttendanceSpecificationBuilder attendanceSpecificationBuilder;
    private final Role adminRole = Role.builder().id(4).build();

    @PostMapping
    public ResponseEntity<MessageDto> addAttendance(
            @RequestBody @NotEmpty @BulkAddAttendanceValid Set<@Valid AttendanceAddRequestControllerDto> dtos,
            @RequestHeader("user_uid") String uid){

        attendanceService.add(dtos.stream()
                .map(AttendanceAddRequestControllerDto::toServiceDto)
                .collect(Collectors.toList()), uid);

        return ResponseEntity.ok(new MessageDto("총 ("+dtos.size()+")개의 Attendance를 성공적으로 추가했습니다!"));
    }


    //TODO 만약 queryParam이 자신일 경우, user0부터 허용, 아닐경우 admin 이상부터 허용
    @GetMapping
    public ResponseEntity<List<AttendanceResponseControllerDto>> getAttendanceAll(
            @PageableDefault(page = 0,size = 1000,sort = "member",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys =
                    {"attendanceType","member","timeTable","index","createdDateTime","modifiedDateTime","createBy","modifiedBy"}
                    ) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam,
            @RequestHeader("user_uid") String uid,
            @RequestHeader("user_role_id") Integer roleID){

        //만약 자신이 아닌 데이터를 검색할려고 하면 권한 인증
        if((!queryParam.containsKey("memberID") || !Objects.equals(queryParam.getFirst("memberID"), uid)) && roleID < adminRole.getId()){
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        //limit 확인 및 추가
        int limit = queryParam.containsKey("limit") ? Integer.parseInt(Objects.requireNonNull(queryParam.getFirst("limit"))) : -1;

        //Specification 등록
        AttendanceSpecification spec = attendanceSpecificationBuilder.toSpec(queryParam);

        List<AttendanceResponseControllerDto> results;

        //limit true
        if(limit > 0){
            results = attendanceService.findAll(spec,PageRequest.of(0, limit, pageable.getSort()))
                    .stream()
                    .map(AttendanceResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }

        else{
            results = attendanceService.findAll(spec,pageable)
                    .stream()
                    .map(AttendanceResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(results);
    }

    @DeleteMapping
    public ResponseEntity<MessageDto> bulkDelAttendance(
            @RequestBody Set<@Valid AttendanceDeleteRequestControllerDto> dtos,
            @RequestHeader("user_uid") String uid){

        attendanceService.delete(dtos.stream().map(AttendanceDeleteRequestControllerDto::toId).collect(Collectors.toSet()));

        return ResponseEntity.ok(new MessageDto("총 ("+dtos.size()+")개의 Attendance를 성공적으로 삭제했습니다!"));
    }

    @PutMapping
    public ResponseEntity<MessageDto> bulkUpdateAttendance(
            @RequestBody @BulkUpdateAttendanceValid @NotNull Set<@Valid AttendanceBulkUpdateRequestControllerDto> dto,
            @RequestHeader("user_uid") String uid){

        attendanceService.update(dto.stream()
                        .map(AttendanceBulkUpdateRequestControllerDto::toServiceDto)
                        .collect(Collectors.toList()),uid);

        return ResponseEntity.ok(new MessageDto("총 ("+dto.size()+")개의 Attendance를 성공적으로 업데이트 했습니다!"));
    }
}
