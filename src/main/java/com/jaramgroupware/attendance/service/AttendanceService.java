package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.attendance.*;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceBulkUpdateRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int batchSize = 100;

    @Transactional
    public AttendanceID add(AttendanceAddServiceDto attendanceAddServiceDto,String who){
        Attendance targetAttendance = attendanceAddServiceDto.toEntity();
        targetAttendance.setCreateBy(who);
        targetAttendance.setModifiedBy(who);

        return attendanceRepository.save(targetAttendance).getId();
    }

    @Transactional
    public void add(List<AttendanceAddServiceDto> attendanceAddServiceDto, String who) {
        List<AttendanceAddServiceDto> batchDto = new ArrayList<>();
        for (AttendanceAddServiceDto dto:attendanceAddServiceDto) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchAdd(batchDto,who);
            }
        }
        if(!batchDto.isEmpty()) {
            batchAdd(batchDto,who);
        }

    }

    public void batchAdd(List<AttendanceAddServiceDto> batchDto,String who){
        List<Attendance> targetAttendances = batchDto.stream()
                .map(AttendanceAddServiceDto::toEntity)
                .collect(Collectors.toList());

        attendanceRepository.bulkInsert(targetAttendances,who);
        targetAttendances.clear();
        batchDto.clear();
    }

    @Transactional(readOnly = true)
    public AttendanceResponseServiceDto findById(AttendanceID id){
        Attendance targetAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ATTENDANCE_ID));
        return new AttendanceResponseServiceDto(targetAttendance);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseServiceDto> findAll(){
        return attendanceRepository.findAllBy()
                .orElseThrow(()->new CustomException(ErrorCode.EMPTY_ATTENDANCE))
                .stream()
                .map(AttendanceResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseServiceDto> findAll(Specification<Attendance> specification){
        return attendanceRepository.findAll(specification)
                .stream()
                .map(AttendanceResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseServiceDto> findAll(Specification<Attendance> specification, Pageable pageable){
        return attendanceRepository.findAll(specification,pageable)
                .stream()
                .map(AttendanceResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceID delete(AttendanceID id){
        Attendance targetAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ATTENDANCE_ID));

        attendanceRepository.delete(targetAttendance);

        return id;
    }

    @Transactional
    public void delete(Set<AttendanceID> ids){
        List<AttendanceID> batchDto = new ArrayList<>();
        for (AttendanceID dto:ids) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchDelete(batchDto);
            }
        }
        if(!batchDto.isEmpty()) {
            batchDelete(batchDto);
        }
    }

    public void batchDelete(List<AttendanceID> batchDto){
        List<Member> members = new ArrayList<>();
        List<TimeTable> timeTables = new ArrayList<>();

        for (AttendanceID dto:batchDto) {
            members.add(dto.getMember());
            timeTables.add(dto.getTimeTable());
        }

        if(attendanceRepository.findAttendancesIn(timeTables,members).size() != batchDto.size())
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_ID);
        attendanceRepository.deleteAllByIdInQuery(timeTables,members);

        members.clear();
        timeTables.clear();
        batchDto.clear();
    }

    @Transactional
    public AttendanceResponseServiceDto update(AttendanceID id, AttendanceUpdateRequestServiceDto attendanceUpdateRequestServiceDto,String who){

        Attendance targetAttendance = attendanceRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ATTENDANCE_ID));

        targetAttendance.update(attendanceUpdateRequestServiceDto.toEntity(),who);

        attendanceRepository.save(targetAttendance);

        return new AttendanceResponseServiceDto(targetAttendance);
    }

    @Transactional
    public void update(List<AttendanceBulkUpdateRequestServiceDto> updateDtos, String who){

        List<AttendanceBulkUpdateRequestServiceDto> batchDto = new ArrayList<>();
        for (AttendanceBulkUpdateRequestServiceDto dto:updateDtos) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchUpdate(batchDto,who);
            }
        }
        if(!batchDto.isEmpty()) {

            batchUpdate(batchDto,who);
        }
    }

    private void batchUpdate(List<AttendanceBulkUpdateRequestServiceDto> batchDto,String who){
        List<Member> members = new ArrayList<>();
        List<TimeTable> timeTables = new ArrayList<>();

        for (AttendanceBulkUpdateRequestServiceDto dto:batchDto) {
            members.add(dto.getMember());
            timeTables.add(dto.getTimeTable());
        }

        if(attendanceRepository.findAttendancesIn(timeTables,members).size() != batchDto.size())
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_ID);

        attendanceRepository.bulkUpdate(batchDto.stream()
                .map(AttendanceBulkUpdateRequestServiceDto::toEntity)
                .collect(Collectors.toList()), who);

        members.clear();
        timeTables.clear();
        batchDto.clear();
    }
}
