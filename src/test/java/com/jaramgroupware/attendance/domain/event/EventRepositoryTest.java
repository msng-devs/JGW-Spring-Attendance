package com.jaramgroupware.attendance.domain.event;

import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.utlis.parse.ParseByNameBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "classpath:tableBuild.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:testDataSet.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class EventRepositoryTest {

    private final TestDataUtils testUtils = new TestDataUtils();
    private final EventSpecificationBuilder eventSpecificationBuilder = new EventSpecificationBuilder(new ParseByNameBuilder());

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findEventById() {
        //given
        Event testGoal = testUtils.getTestEvent();
        //when
        Event result = eventRepository.findEventById(1L)
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertEquals(result.toString(),testGoal.toString());
    }

    @Test
    void findAllBy() {
        //given
        List<Event> testGoal = new ArrayList<Event>();
        testGoal.add(testUtils.getTestEvent());
        testGoal.add(testUtils.getTestEvent2());
        //when
        List<Event> results = eventRepository.findAllBy()
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertThat(testUtils.isListSame(testGoal,results),is(true));
    }

    @Test
    void save() {
        //given
        Event testGoal = testUtils.getTestEvent();
        testGoal.setId(null);

        //when
        eventRepository.save(testGoal);

        //then
        testGoal.setId(3L);
        assertEquals(testGoal.toString(),testEntityManager.find(Event.class,3L).toString());
    }

    @Test
    void delete() {
        //given
        Event testGoal = testUtils.getTestEvent();

        //when
        eventRepository.deleteEventById(testGoal.getId());

        //then
        assertThat(testEntityManager.find(Event.class,testGoal.getId()),is(nullValue()));
    }

    @Test
    void findAllWithIntegratedSpec(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testEvent.getCreateBy());
        queryParam.add("createBy",testEvent.getCreateBy());
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testEvent.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startCreatedDateTime",testEvent.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testEvent.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testEvent.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testEvent.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("name","좋아");
        queryParam.add("index","영업하나요");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIntegratedSpec2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testEvent.getCreateBy());
        queryParam.add("createBy",testEvent.getCreateBy());
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testEvent.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startCreatedDateTime",testEvent.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testEvent.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testEvent.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testEvent.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("name","축제");
        queryParam.add("index","영업합니다2");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testEvent.getCreateBy());

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testEvent.getCreateBy());

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testEvent.getCreateBy());

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testEvent.getCreateBy());

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event - DateTime 검색, 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithDateTimeWithStartAndEnd(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testEvent.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event - DateTime 검색, 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithStartAndEnd2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testEvent.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event - DateTime 검색, 시작 범위만 주어졌을 때")
    @Test
    void findAllWithDateTimeWithStart(){
        //given
        Event testEvent = testUtils.getTestEvent();
        Event testEvent2 = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvent,testEvent2)));
    }

    @DisplayName("Event - DateTime 검색, 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithStart2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event - DateTime 검색, 종료 범위만 주어졌을 때")
    @Test
    void findAllWithDateTimeWithEnd(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event - DateTime 검색, 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithEnd2(){
        //given
        Event testEvent = testUtils.getTestEvent2();
        Event testEvent2 = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endDateTime",testEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvent,testEvent2)));
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd(){
        //given
        Event testEvents = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd2(){
        //given
        Event testEvents = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStart(){
        //given
        Event testEvents = testUtils.getTestEvent();
        Event testEvents2 = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvents,testEvents2)));
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStart2(){
        //given
        Event testEvents = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithEnd(){
        //given
        Event testEvents = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithEnd2(){
        //given
        Event testEvents = testUtils.getTestEvent2();
        Event testEvents2 = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testEvents.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvents,testEvents2)));
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd(){
        //given
        Event testEvents = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd2(){
        //given
        Event testEvents = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStart(){
        //given
        Event testEvents = testUtils.getTestEvent();
        Event testEvents2 = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvents,testEvents2)));
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStart2(){
        //given
        Event testEvents = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvents.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithEnd(){
        //given
        Event testEvents = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertThat(res.stream().filter(r -> r.equals(testEvents)).findFirst(),is(notNullValue()));
    }

    @DisplayName("Event modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithEnd2(){
        //given
        Event testEvents = testUtils.getTestEvent2();
        Event testEvents2 = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testEvents.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvents,testEvents2)));
    }

    @Test
    void findAllWithName(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name","좋아");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithName2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name","축제");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIndex(){
        //given
        Event testEvent = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("index","하나요");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIndex2(){
        //given
        Event testEvent = testUtils.getTestEvent2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("index","합니다");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testEvent.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAll(){
        //given
        Event testEvents = testUtils.getTestEvent2();
        Event testEvents2 = testUtils.getTestEvent();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("this is not working param","'hos'");

        EventSpecification testSpec = eventSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Event> res = eventRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testEvents,testEvents2)));
    }
}