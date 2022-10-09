package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.event.EventRepository;
import com.jaramgroupware.attendance.domain.event.EventSpecification;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventUpdateRequestServiceDto;
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
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

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
        EventAddRequestServiceDto testServiceDto = EventAddRequestServiceDto
                .builder()
                .starDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .name("test")
                .index("test")
                .build();

        Event testEntity = testServiceDto.toEntity();
        testEntity.setId(1L);

        doReturn(testEntity).when(eventRepository).save(any());

        //when
        Long resultID = eventService.add(testServiceDto,"system");

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(resultID, Objects.requireNonNull(resultID));
        verify(eventRepository).save(any());
    }

    @Test
    void findById() {

        //given
        Long testID = 1L;
        Event testEntity = testUtils.getTestEvent();

        doReturn(Optional.of(testEntity)).when(eventRepository).findById(testID);

        //when
        EventResponseServiceDto result = eventService.findById(testID);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.toString(), Objects.requireNonNull(result).toString());
        verify(eventRepository).findById(testID);
    }

    @Test
    void findAll() {
        //given
        List<Event> testList = new ArrayList<Event>();

        Event testEntity1 = testUtils.getTestEvent();
        testList.add(testEntity1);

        Event testEntity2 = testUtils.getTestEvent2();
        testList.add(testEntity2);

        doReturn(Optional.of(testList)).when(eventRepository).findAllBy();

        //when
        List<EventResponseServiceDto> results = eventService.findAll();

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(EventResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(eventRepository).findAllBy();
    }

    @Test
    void findAllWithSpecAndPage() {
        //given
        List<Event> testList = new ArrayList<Event>();

        Event testEntity1 = testUtils.getTestEvent();
        testList.add(testEntity1);

        Event testEntity2 = testUtils.getTestEvent2();
        testList.add(testEntity2);
        Specification<Event> testSpec = Mockito.mock(EventSpecification.class);
        Pageable testPage = Mockito.mock(Pageable.class);
        Page<Event> testRes = new PageImpl<>(testList);

        doReturn(testRes).when(eventRepository).findAll(testSpec,testPage);

        //when
        List<EventResponseServiceDto> results = eventService.findAll(testSpec,testPage);

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(EventResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(eventRepository).findAll(testSpec,testPage);
    }

    @Test
    void delete() {

        //given
        Long testID = 1L;
        Event testEntity = testUtils.getTestEvent();
        doReturn(Optional.of(testEntity)).when(eventRepository).findById(testID);

        //when
        Long resultID = eventService.delete(testID);

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(testID, Objects.requireNonNull(resultID));
        verify(eventRepository).findById(testID);
        verify(eventRepository).delete(any());
    }

    @Test
    void update() {
        //given
        Long testID = 1L;
        EventUpdateRequestServiceDto testDto = EventUpdateRequestServiceDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .index("test")
                .name("test")
                .build();

        Event targetEntity = testUtils.getTestEvent();

        doReturn(Optional.of(targetEntity)).when(eventRepository).findById(testID);

        //when
        EventResponseServiceDto result = eventService.update(testID,testDto,testUtils.getTestUid());

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testDto.getIndex(), Objects.requireNonNull(result).getIndex());
        Assertions.assertEquals(testDto.getName(), Objects.requireNonNull(result).getName());
        Assertions.assertEquals(testDto.getStartDateTime(),Objects.requireNonNull(result).getStartDateTime());
        Assertions.assertEquals(testDto.getEndDateTime(),Objects.requireNonNull(result).getEndDateTime());
        verify(eventRepository).findById(testID);
        verify(eventRepository).save(any());

    }
}