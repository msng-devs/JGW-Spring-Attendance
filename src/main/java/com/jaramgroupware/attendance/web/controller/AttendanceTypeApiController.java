package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.dto.attendanceType.controllerDto.AttendanceTypeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceTypeService;
import com.jaramgroupware.attendance.web.aop.authorization.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attendanceType")
@RestController
public class AttendanceTypeApiController {

    private final AttendanceTypeService attendanceTypeService;

    @Auth
    @GetMapping
    public ResponseEntity<List<AttendanceTypeResponseControllerDto>> findAllAttendanceType(){
        var data = attendanceTypeService.findAll();
        return ResponseEntity.ok(data.stream().map(AttendanceTypeResponseServiceDto::toControllerDto).collect(Collectors.toList()));
    }
}
