package com.jaramgroupware.attendance.domain.timeTable;

import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.utils.parse.ParseByNameBuilder;
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
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "classpath:tableBuild.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:testDataSet.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class TimeTableRepositoryTest {

    private final TestUtils testUtils = new TestUtils();
    private final TimeTableSpecificationBuilder timeTableSpecificationBuilder = new TimeTableSpecificationBuilder(new ParseByNameBuilder());
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findTimeTableById() {
        //given
        TimeTable testGoal = testUtils.getTestTimeTable();
        //when
        TimeTable result = timeTableRepository.findTimeTableById(testGoal.getId())
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertEquals(result.toString(),testGoal.toString());
    }

    @Test
    void findAllBy() {
        //given
        List<TimeTable> testGoal = new ArrayList<TimeTable>();
        testGoal.add(testUtils.getTestTimeTable());
        testGoal.add(testUtils.getTestTimeTable2());
        //when
        List<TimeTable> results = timeTableRepository.findAllBy()
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertThat(testUtils.isListSame(testGoal,results),is(true));
    }

    @Test
    void save() {
        //given
        TimeTable testGoal = testUtils.getTestTimeTable();
        testGoal.setId(null);

        //when
        timeTableRepository.save(testGoal);

        //then
        testGoal.setId(3L);
        assertEquals(testGoal.toString(),testEntityManager.find(TimeTable.class,testGoal.getId()).toString());
    }

    @Test
    void delete() {
        //given
        TimeTable testGoal = testUtils.getTestTimeTable();

        //when
        timeTableRepository.delete(testGoal);

        //then
        assertThat(testEntityManager.find(TimeTable.class,testGoal.getId()),is(nullValue()));
    }

    @Test
    void findAllWithIntegratedSpec(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("timeTableID",testTimeTable.getEvent().getId().toString());
        queryParam.add("modifiedBy",testTimeTable.getModifiedBy());
        queryParam.add("createBy",testTimeTable.getCreateBy());
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("name",testTimeTable.getName());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIntegratedSpec2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("timeTableID",testTimeTable.getEvent().getId().toString());
        queryParam.add("modifiedBy",testTimeTable.getModifiedBy());
        queryParam.add("createBy",testTimeTable.getCreateBy());
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("name",testTimeTable.getName());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithEventID(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("eventID",testTimeTable.getEvent().getId().toString());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithEventID2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("eventID",testTimeTable.getEvent().getId().toString());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testTimeTable.getModifiedBy());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testTimeTable.getModifiedBy());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testTimeTable.getCreateBy());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testTimeTable.getCreateBy());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithDateTimeWith(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable - DateTime 검색, 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithDateTimeWithStartAndEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable - DateTime 검색, 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithStartAndEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable - DateTime 검색, 시작 범위만 주어졌을 때")
    @Test
    void findAllWithDateTimeWithStart(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(), Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @DisplayName("TimeTable - DateTime 검색, 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithStart2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable - DateTime 검색, 종료 범위만 주어졌을 때")
    @Test
    void findAllWithDateTimeWithEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable - DateTime 검색, 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithDateTimeWithEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStart(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStart2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStart(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStart2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithEnd(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertThat(res.stream().filter(r -> r.equals(testTimeTable)).findFirst(),is(notNullValue()));
    }

    @DisplayName("TimeTable modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithEnd2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();
        TimeTable testTimeTable2 = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testTimeTable,testTimeTable2)));
    }

    @Test
    void findAllWithName(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name",testTimeTable.getName());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithName2(){
        //given
        TimeTable testTimeTable = testUtils.getTestTimeTable2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name",testTimeTable.getName());

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testTimeTable.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAll(){
        //given

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("this is pseudo param ","not working");

        TimeTableSpecification testSpec = timeTableSpecificationBuilder.toSpec(queryParam);

        //when
        Page<TimeTable> res = timeTableRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testUtils.getTestMember(),testUtils.getTestMember2())));
    }

}