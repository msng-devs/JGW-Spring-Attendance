package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableSpecification;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableUpdateRequestServiceDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TimeTableServiceTest {

    @InjectMocks
    private TimeTableService timeTableService;

    @Mock
    private TimeTableRepository timeTableRepository;

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
        TimeTableAddRequestServiceDto testServiceDto = TimeTableAddRequestServiceDto
                .builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .event(testUtils.getTestEvent())
                .name("test")
                .build();

        TimeTable testEntity = testServiceDto.toEntity();
        testEntity.setId(testUtils.getTestTimeTable().getId());

        doReturn(testEntity).when(timeTableRepository).save(any());

        //when
        Long resultID = timeTableService.add(testServiceDto,"system");

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(resultID, Objects.requireNonNull(resultID));
        verify(timeTableRepository).save(any());
    }

    @Test
    void findById() {

        //given
        Long testID = 1L;
        TimeTable testEntity = testUtils.getTestTimeTable();

        doReturn(Optional.of(testEntity)).when(timeTableRepository).findTimeTableById(testID);

        //when
        TimeTableResponseServiceDto result = timeTableService.findById(testID);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.toString(), Objects.requireNonNull(result).toString());
        verify(timeTableRepository).findTimeTableById(testID);
    }

    @Test
    void findAll() {
        //given
        List<TimeTable> testList = new ArrayList<TimeTable>();

        TimeTable testEntity1 = testUtils.getTestTimeTable();
        testList.add(testEntity1);

        TimeTable testEntity2 = testUtils.getTestTimeTable2();
        testList.add(testEntity2);

        doReturn(Optional.of(testList)).when(timeTableRepository).findAllBy();

        //when
        List<TimeTableResponseServiceDto> results = timeTableService.findAll();

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(TimeTableResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(timeTableRepository).findAllBy();
    }

    @Test
    void findAllWithSpecAndPAge() {
        //given
        List<TimeTable> testList = new ArrayList<TimeTable>();

        TimeTable testEntity1 = testUtils.getTestTimeTable();
        testList.add(testEntity1);

        TimeTable testEntity2 = testUtils.getTestTimeTable2();
        testList.add(testEntity2);

        Specification<TimeTable> testSpec = Mockito.mock(TimeTableSpecification.class);
        Pageable testPage = Mockito.mock(Pageable.class);
        Page<TimeTable> res = new PageImpl<>(testList);

        doReturn(res).when(timeTableRepository).findAll(testSpec,testPage);

        //when
        List<TimeTableResponseServiceDto> results = timeTableService.findAll(testSpec,testPage);

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(TimeTableResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(timeTableRepository).findAll(testSpec,testPage);
    }

    @Test
    void delete() {

        //given
        Long testID = 1L;
        TimeTable testEntity = testUtils.getTestTimeTable();
        doReturn(Optional.of(testEntity)).when(timeTableRepository).findById(testID);

        //when
        Long resultID = timeTableService.delete(testID);

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(testID, Objects.requireNonNull(resultID));
        verify(timeTableRepository).findById(testID);
        verify(timeTableRepository).delete(any());
    }

    @Test
    void update() {
        //given
        Long testID = 1L;
        TimeTableUpdateRequestServiceDto testDto = TimeTableUpdateRequestServiceDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .name("test")
                .build();

        TimeTable targetEntity = testUtils.getTestTimeTable();

        doReturn(Optional.of(targetEntity)).when(timeTableRepository).findById(testID);

        //when
        TimeTableResponseServiceDto result = timeTableService.update(testID,testDto, testUtils.getTestUid());

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testDto.getStartDateTime(),Objects.requireNonNull(result).getStartDateTime());
        Assertions.assertEquals(testDto.getEndDateTime(),Objects.requireNonNull(result).getEndDateTime());
        Assertions.assertEquals(testDto.getName(),Objects.requireNonNull(result).getName());
        verify(timeTableRepository).findById(testID);
        verify(timeTableRepository).save(any());

    }
}