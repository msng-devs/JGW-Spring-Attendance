package com.jaramgroupware.attendance.web;

import com.jaramgroupware.attendance.dto.attendanceType.controllerDto.AttendanceTypeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/attendanceType")
public class AttendanceTypeApiController {

    private final AttendanceTypeService attendanceTypeService;

    @GetMapping("{rankId}")
    public ResponseEntity<AttendanceTypeResponseControllerDto> getAttendanceTypeById(
            @PathVariable Integer rankId){

        AttendanceTypeResponseControllerDto result = attendanceTypeService.findById(rankId).toControllerDto();

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceTypeResponseControllerDto>> getAttendanceTypeAll(){

        List<AttendanceTypeResponseControllerDto> results = attendanceTypeService.findAll()
                .stream().map(AttendanceTypeResponseServiceDto::toControllerDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }


}
