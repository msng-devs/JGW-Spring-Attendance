package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendance.AttendanceID;
import com.jaramgroupware.attendance.domain.attendance.AttendanceRepository;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceBulkUpdateRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ComponentScan
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private AttendanceRepository attendanceRepository;

    private final TestUtils testUtils = new TestUtils();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() {
        //given
        AttendanceAddServiceDto testServiceDto = AttendanceAddServiceDto
                .builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .member(testUtils.getTestMember())
                .timeTable(testUtils.getTestTimeTable())
                .index("test")
                .build();

        Attendance testEntity = testServiceDto.toEntity();
        doReturn(testEntity).when(attendanceRepository).save(any());

        //when
        AttendanceID resultID = attendanceService.add(testServiceDto,"system");

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(resultID, Objects.requireNonNull(resultID));
        verify(attendanceRepository).save(any());
    }

    @Test
    void addAll() {
        //given
        List<AttendanceAddServiceDto> testDtos = new ArrayList<>();

        AttendanceAddServiceDto testServiceDto = AttendanceAddServiceDto
                .builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .member(testUtils.getTestMember())
                .timeTable(testUtils.getTestTimeTable())
                .index("test")
                .build();
        testDtos.add(testServiceDto);

        AttendanceAddServiceDto testServiceDto2 = AttendanceAddServiceDto
                .builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .member(testUtils.getTestMember2())
                .timeTable(testUtils.getTestTimeTable2())
                .index("test")
                .build();
        testDtos.add(testServiceDto2);

        //when
        attendanceService.add(testDtos,testUtils.getTestUid());

        //then
        verify(attendanceRepository).bulkInsert(any(), any());
    }

    @Test
    void findById() {

        //given
        AttendanceID testID = testUtils.getTestAttendance().getId();
        Attendance testEntity = testUtils.getTestAttendance();

        doReturn(Optional.of(testEntity)).when(attendanceRepository).findById(testID);

        //when
        AttendanceResponseServiceDto result = attendanceService.findById(testID);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.toString(), Objects.requireNonNull(result).toString());
        verify(attendanceRepository).findById(testID);
    }

    @Test
    void findAll() {
        //given
        List<Attendance> testList = new ArrayList<Attendance>();

        Attendance testEntity1 = testUtils.getTestAttendance();
        testList.add(testEntity1);

        Attendance testEntity2 = testUtils.getTestAttendance2();
        testList.add(testEntity2);

        doReturn(Optional.of(testList)).when(attendanceRepository).findAllBy();

        //when
        List<AttendanceResponseServiceDto> results = attendanceService.findAll();

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(AttendanceResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(attendanceRepository).findAllBy();
    }

    @Test
    void findAllWithSpec() {
        //given
        List<Attendance> testList = new ArrayList<Attendance>();

        Attendance testEntity1 = testUtils.getTestAttendance();
        testList.add(testEntity1);

        Attendance testEntity2 = testUtils.getTestAttendance2();
        testList.add(testEntity2);
        Specification<Attendance> testSpec = Mockito.mock(AttendanceSpecification.class);

        doReturn(testList).when(attendanceRepository).findAll(testSpec);

        //when
        List<AttendanceResponseServiceDto> results = attendanceService.findAll(testSpec);

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(AttendanceResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(attendanceRepository).findAll(testSpec);
    }
    @Test
    void findAllWithSpecAndPage() {
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
        Assertions.assertEquals(testList.stream().map(AttendanceResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(attendanceRepository).findAll(testSpec,testPage);
    }

    @Test
    void delete() {

        //given
        AttendanceID testID = testUtils.getTestAttendance().getId();
        Attendance testEntity = testUtils.getTestAttendance();
        doReturn(Optional.of(testEntity)).when(attendanceRepository).findById(testID);

        //when
        AttendanceID resultID = attendanceService.delete(testID);

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(testID, Objects.requireNonNull(resultID));
        verify(attendanceRepository).findById(testID);
        verify(attendanceRepository).delete(any());
    }

    @Test
    void deleteAll() {

        //given
        Set<AttendanceID> testID = new HashSet<>();

        testID.add(testUtils.testAttendance.getId());
        testID.add(testUtils.testAttendance2.getId());

        doReturn(Arrays.asList(testUtils.getTestAttendance(),testUtils.getTestAttendance2()))
                .when(attendanceRepository)
                .findAttendancesIn(any(),any());

        //when
        attendanceService.delete(testID);

        //then
        verify(attendanceRepository).findAttendancesIn(any(),any());
        verify(attendanceRepository).deleteAllByIdInQuery(any(),any());

    }

    @Test
    void update() {
        //given
        AttendanceUpdateRequestServiceDto testDto = AttendanceUpdateRequestServiceDto.builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .index("test")
                .build();

        Attendance targetEntity = testUtils.getTestAttendance();

        doReturn(Optional.of(targetEntity)).when(attendanceRepository).findById(targetEntity.getId());

        //when
        AttendanceResponseServiceDto result = attendanceService.update(targetEntity.getId(),testDto, testUtils.getTestUid());

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testDto.getIndex(), Objects.requireNonNull(result).getIndex());
        Assertions.assertEquals(testDto.getAttendanceType().getId(), Objects.requireNonNull(result).getAttendanceTypeID());
        verify(attendanceRepository).findById(targetEntity.getId());
        verify(attendanceRepository).save(any());

    }

    @Test
    void updateAll() {
        //given
        List<AttendanceBulkUpdateRequestServiceDto> testDtos = new ArrayList<>();

        AttendanceBulkUpdateRequestServiceDto testDto = AttendanceBulkUpdateRequestServiceDto.builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .index("test")
                .build();
        testDtos.add(testDto);
        AttendanceBulkUpdateRequestServiceDto testDto2 = AttendanceBulkUpdateRequestServiceDto.builder()
                .attendanceType(testUtils.getTestAttendanceType())
                .index("test")
                .build();
        testDtos.add(testDto2);

        doReturn(Arrays.asList(testUtils.getTestAttendance(),testUtils.getTestAttendance2()))
                .when(attendanceRepository)
                .findAttendancesIn(any(),any());

        //when
        attendanceService.update(testDtos, testUtils.getTestUid());

        //then
        verify(attendanceRepository).findAttendancesIn(any(),any());
        verify(attendanceRepository).bulkUpdate(any(),any());

    }
}