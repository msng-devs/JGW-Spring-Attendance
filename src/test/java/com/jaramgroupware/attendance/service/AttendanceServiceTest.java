package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendance.AttendanceRepository;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.domain.member.MemberRepository;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.management.ServiceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ComponentScan
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private AttendanceTypeRepository attendanceTypeRepository;

    private final TestDataUtils testUtils = new TestDataUtils();
    private final LocalDateTime testDateTime = LocalDateTime.parse("2023-08-28T21:25:23.749555800");

    @DisplayName("createAttendance - 정상적인 출결 생성 정보를 받으면, 새로운 출결을 생성한다.")
    @Test
    void createAttendance() {
        //given
        var testMember = testUtils.getTestMember();
        var testTimeTable = testUtils.getTestTimeTable();
        var testAttendanceType = testUtils.getTestAttendanceType();

        var testRequestDto = AttendanceAddRequestServiceDto
                .builder()
                .memberId(testMember.getId())
                .index("test")
                .timeTableId(testTimeTable.getId())
                .attendanceTypeId(testAttendanceType.getId())
                .createdBy("test")
                .build();

        var testNewAttendance = testRequestDto.toEntity(testMember, testAttendanceType,testTimeTable);
        var testSavedNewAttendance = Attendance.builder()
                .id(1L)
                .attendanceType(testAttendanceType)
                .index("test")
                .member(testMember)
                .timeTable(testTimeTable)
                .build();
        testNewAttendance.setCreateBy("test");
        testNewAttendance.setModifiedBy("test");
        testNewAttendance.setCreatedDateTime(testDateTime);
        testNewAttendance.setModifiedDateTime(testDateTime);

        var exceptResult = new AttendanceResponseServiceDto(testSavedNewAttendance);

        doReturn(Optional.of(testMember)).when(memberRepository).findById(testRequestDto.getMemberId());
        doReturn(Optional.of(testTimeTable)).when(timeTableRepository).findById(testRequestDto.getTimeTableId());
        doReturn(Optional.of(testAttendanceType)).when(attendanceTypeRepository).findById(testRequestDto.getAttendanceTypeId());
        doReturn(testSavedNewAttendance).when(attendanceRepository).save(testNewAttendance);
        //when
        var testResult = attendanceService.createAttendance(testRequestDto);

        //then
        assertEquals(exceptResult, testResult);
        verify(memberRepository).findById(testRequestDto.getMemberId());
        verify(attendanceTypeRepository).findById(testRequestDto.getAttendanceTypeId());
        verify(timeTableRepository).findById(testRequestDto.getTimeTableId());
        verify(attendanceRepository).save(testNewAttendance);

    }

    @DisplayName("findAttendanceById - 정상적인 출결 id를 받으면, 해당 출결을 반환한다.")
    @Test
    void findAttendanceById() {
        //given
        var testAttendance = testUtils.getTestAttendance();
        var testId = testAttendance.getId();
        var execptResult = new AttendanceResponseServiceDto(testAttendance);

        doReturn(Optional.of(testAttendance)).when(attendanceRepository).findById(testId);

        //when
        var result = attendanceService.findAttendanceById(testId);

        //then
        assertEquals(execptResult, result);
        verify(attendanceRepository).findById(testId);

    }

    @DisplayName("findAttendanceById - 비정상적인 출결 id를 받으면, ServiceNotFoundException을 반환한다.")
    @Test
    void findAttendanceById2() {
        //given
        var testAttendance = testUtils.getTestAttendance();
        var testId = testAttendance.getId();

        doReturn(Optional.empty()).when(attendanceRepository).findById(testId);

        //when
        assertThrows(ServiceException.class, () -> attendanceService.findAttendanceById(1L));


        //then
        verify(attendanceRepository).findById(testId);

    }

    @DisplayName("updateAttendance - 정상적인 출결 id와 변경된 attendanceType을 받으면, 해당 출결을 수정한다.")
    @Test
    void updateAttendance() {
        //given
        var testAttendance = testUtils.getTestAttendance();

        var testUpdateRequestDto = AttendanceUpdateRequestServiceDto.builder()
                .attendanceId(testAttendance.getId())
                .attendanceTypeId(testUtils.getTestAttendanceType2().getId())
                .index("test2")
                .modifiedBy("test")
                .build();

        var updatedAttendance = Attendance.builder()
                .id(1L)
                .member(testUtils.getTestMember())
                .attendanceType(testUtils.getTestAttendanceType2())
                .timeTable(testUtils.getTestTimeTable())
                .index("test2")
                .build();

        updatedAttendance.setModifiedBy("test");
        updatedAttendance.setModifiedDateTime(testDateTime);

        doReturn(Optional.of(testAttendance)).when(attendanceRepository).findById(testAttendance.getId());
        doReturn(Optional.of(testUtils.getTestAttendanceType2())).when(attendanceTypeRepository).findById(testUpdateRequestDto.getAttendanceTypeId());
        doReturn(updatedAttendance).when(attendanceRepository).save(updatedAttendance);

        var exceptResult = new AttendanceResponseServiceDto(updatedAttendance);
        //when
        var result = attendanceService.updateAttendance(testUpdateRequestDto);

        //then
        assertEquals(exceptResult, result);
        verify(attendanceRepository).findById(testAttendance.getId());
        verify(attendanceTypeRepository).findById(testUpdateRequestDto.getAttendanceTypeId());
        verify(attendanceRepository).save(updatedAttendance);
    }

    @DisplayName("updateAttendance - 정상적인 출결 id와 변경되지 않은 attendanceType을 받으면, 해당 출결을 수정한다.")
    @Test
    void updateAttendance2() {
        //given
        var testAttendance = testUtils.getTestAttendance();

        var testUpdateRequestDto = AttendanceUpdateRequestServiceDto.builder()
                .attendanceId(testAttendance.getId())
                .attendanceTypeId(testUtils.getTestAttendanceType().getId())
                .index("test2")
                .modifiedBy("test")
                .build();

        var updatedAttendance = testUtils.getTestAttendance();
        updatedAttendance.update(testUpdateRequestDto.toEntity());

        doReturn(Optional.of(testAttendance)).when(attendanceRepository).findById(testAttendance.getId());
        doReturn(updatedAttendance).when(attendanceRepository).save(updatedAttendance);
        var exceptResult = new AttendanceResponseServiceDto(updatedAttendance);

        //when
        var result = attendanceService.updateAttendance(testUpdateRequestDto);

        //then
        assertEquals(exceptResult, result);
        verify(attendanceRepository).findById(testAttendance.getId());
        verify(attendanceRepository).save(updatedAttendance);
    }

    @Test
    void findAll() {
        //given
        List<Attendance> testList = new ArrayList<Attendance>();

        Attendance testEntity1 = testUtils.getTestAttendance();
        testList.add(testEntity1);

        Attendance testEntity2 = testUtils.getTestAttendance2();
        testList.add(testEntity2);
        Specification<Attendance> testSpec = Mockito.mock(AttendanceSpecification.class);
        Pageable testPage = Mockito.mock(Pageable.class);

        Page<Attendance> testRes = new PageImpl<Attendance>(testList);

        doReturn(testRes).when(attendanceRepository).findAll(testSpec,testPage);

        //when
        List<AttendanceResponseServiceDto> results = attendanceService.findAll(testSpec,testPage);

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(AttendanceResponseServiceDto::new).toList().toString(), Objects.requireNonNull(results).toString());
        verify(attendanceRepository).findAll(testSpec,testPage);
    }
}