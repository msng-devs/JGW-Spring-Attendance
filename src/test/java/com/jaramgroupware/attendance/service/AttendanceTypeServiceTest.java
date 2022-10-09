package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ComponentScan
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AttendanceTypeServiceTest {

    @InjectMocks
    private AttendanceTypeService attendanceTypeService;

    @Mock
    private AttendanceTypeRepository attendanceTypeRepository;

    private final TestUtils testUtils = new TestUtils();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById() {
        //given
        Integer testID = 1;
        AttendanceType testEntity = AttendanceType.builder()
                .id(testID)
                .name("test")
                .build();

        doReturn(Optional.of(testEntity)).when(attendanceTypeRepository).findById(testID);

        //when
        AttendanceTypeResponseServiceDto result = attendanceTypeService.findById(testID);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.toString(), Objects.requireNonNull(result).toString());
        verify(attendanceTypeRepository).findById(testID);
    }

    @Test
    void findAll() {
        //given
        List<AttendanceType> testList = new ArrayList<AttendanceType>();

        AttendanceType testEntity1 = AttendanceType.builder()
                .id(1)
                .name("test")
                .build();
        testList.add(testEntity1);

        AttendanceType testEntity2 = AttendanceType.builder()
                .id(2)
                .name("test")
                .build();
        testList.add(testEntity2);

        doReturn(Optional.of(testList)).when(attendanceTypeRepository).findAllBy();

        //when
        List<AttendanceTypeResponseServiceDto> results = attendanceTypeService.findAll();

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(AttendanceTypeResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(attendanceTypeRepository).findAllBy();
    }
}