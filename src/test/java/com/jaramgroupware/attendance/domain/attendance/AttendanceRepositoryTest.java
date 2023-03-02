package com.jaramgroupware.attendance.domain.attendance;


import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.utlis.parse.ParseByNameBuilder;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "classpath:tableBuild.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:testDataSet.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class AttendanceRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private final AttendanceSpecificationBuilder attendanceSpecificationBuilder = new AttendanceSpecificationBuilder(new ParseByNameBuilder());

    @Autowired
    private AttendanceRepository attendanceRepository;

    private final TestDataUtils testUtils = new TestDataUtils();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("deleteAttendanceById 테스트 - 2개의 attendance의 Id가 주어지면, 두 Attendance를 삭제한다.")
    @Test
    void deleteAttendanceById() {
        //given
        Attendance testGoal = testUtils.getTestAttendance();

        //when
        attendanceRepository.deleteAttendanceById(testGoal.getId());

        //then
        assertThat(testEntityManager.find(Attendance.class,testGoal.getId()),is(nullValue()));
    }

    @Test
    void findAll(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();
        Attendance testAttendances2 = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("this is not working param",testAttendances.getMember().getId());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        Assertions.assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
    }

    @Test
    void findAllWithAttendanceType(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithAttendanceType2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithMember(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMember().getId());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithMember2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMember().getId());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithTimeTable(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithTimeTable2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreatedBy(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testAttendances.getCreateBy());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreatedBy2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testAttendances.getCreateBy());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testAttendances.getCreateBy());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testAttendances.getCreateBy());

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStart(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();
        Attendance testAttendances2 = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStart2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithEnd(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithEnd2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();
        Attendance testAttendances2 = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStart(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();
        Attendance testAttendances2 = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStart2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithEnd(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithEnd2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();
        Attendance testAttendances2 = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
    }

    @Test
    void findAllWithIndex(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("index","출결 코드를");

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());

    }

    @Test
    void findAllWithIndex2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("index","이것은");

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());

    }

    @Test
    void findAllWithIntegratedSpec(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
        queryParam.add("memberID",testAttendances.getMember().getId());
        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","출결 코드를");

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIntegratedSpec2(){
        //given
        Attendance testAttendances = testUtils.getTestAttendance2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
        queryParam.add("memberID",testAttendances.getMember().getId());
        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","이것은");

        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);

        //when
        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
    }
}