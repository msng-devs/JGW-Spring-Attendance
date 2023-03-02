//package com.jaramgroupware.attendance.domain.attendance;
//
//import com.jaramgroupware.attendance.TestUtils;
//import com.jaramgroupware.attendance.domain.member.Member;
//import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
//import com.jaramgroupware.attendance.utils.parse.ParseByNameBuilder;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.jdbc.SqlGroup;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//
//@ActiveProfiles("test")
//@SqlGroup({
//        @Sql(scripts = "classpath:tableBuild.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
//        @Sql(scripts = "classpath:testDataSet.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//})
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ExtendWith(SpringExtension.class)
//@Transactional
//@DataJpaTest
//class AttendanceRepositoryTest {
//
//    @Autowired
//    private TestEntityManager testEntityManager;
//
//    private final AttendanceSpecificationBuilder attendanceSpecificationBuilder = new AttendanceSpecificationBuilder(new ParseByNameBuilder());
//
//    @Autowired
//    private AttendanceRepository attendanceRepository;
//
//    private final TestUtils testUtils = new TestUtils();
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void findAttendancesIn() {
//        //given
//        List<Attendance> testGoal = Arrays.asList(testUtils.getTestAttendance(),testUtils.getTestAttendance2());
//
//        List<TimeTable> testGoalTimeTables = Arrays.asList(testUtils.getTestAttendance().getTimeTable(),testUtils.getTestAttendance2().getTimeTable());
//
//        List<Member> testGoalMember = Arrays.asList(testUtils.getTestAttendance().getMember(),testUtils.getTestAttendance2().getMember());
//
//        //when
//        List<Attendance> result = attendanceRepository.findAttendancesIn(testGoalTimeTables,testGoalMember);
//
//        //then
//        assertEquals(testGoal.toString(),result.toString());
//    }
//
//    @Test
//    void findAllBy() {
//        //given
//        List<Attendance> testGoal = Arrays.asList(testUtils.getTestAttendance(),testUtils.getTestAttendance2());
//
//        //when
//        List<Attendance> results = attendanceRepository.findAllBy()
//                .orElseThrow(IllegalArgumentException::new);
//        //then
//        assertThat(testUtils.isListSame(testGoal,results),is(true));
//    }
//
//    @Test
//    void deleteAllByIdInQuery() {
//        //given
//        List<Attendance> testGoal = Arrays.asList(testUtils.getTestAttendance(),testUtils.getTestAttendance2());
//
//        List<TimeTable> testGoalTimeTables = Arrays.asList(testUtils.getTestAttendance().getTimeTable(),testUtils.getTestAttendance2().getTimeTable());
//
//        List<Member> testGoalMember = Arrays.asList(testUtils.getTestAttendance().getMember(),testUtils.getTestAttendance2().getMember());
//
//        //when
//        attendanceRepository.deleteAllByIdInQuery(testGoalTimeTables,testGoalMember);
//
//        //then
//        assertThat(testEntityManager.find(Attendance.class,testGoal.get(0).getId()),is(nullValue()));
//        assertThat(testEntityManager.find(Attendance.class,testGoal.get(1).getId()),is(nullValue()));
//    }
//
//    @Test
//    void bulkInsert(){
//        //given
//        List<Attendance> testAttendances = new ArrayList<>();
//
//        Attendance firTestAttendance = Attendance.builder()
//                .index("test")
//                .attendanceType(testUtils.getTestAttendanceType())
//                .timeTable(testUtils.getTestTimeTable2())
//                .member(testUtils.getTestMember())
//                .build();
//        testAttendances.add(firTestAttendance);
//
//        Attendance secTestAttendance = Attendance.builder()
//                .index("test")
//                .attendanceType(testUtils.getTestAttendanceType())
//                .timeTable(testUtils.getTestTimeTable())
//                .member(testUtils.getTestMember2())
//                .build();
//        testAttendances.add(secTestAttendance);
//
//        //when
//        attendanceRepository.bulkInsert(testAttendances,testUtils.getTestUid());
//
//        //then
//        Attendance firResAttendance = testEntityManager.find(Attendance.class,firTestAttendance.getId());
//        Attendance secResAttendance = testEntityManager.find(Attendance.class,secTestAttendance.getId());
//
//        assertThat(firResAttendance,is(notNullValue()));
//        assertEquals(firTestAttendance.getIndex(),firResAttendance.getIndex());
//        assertEquals(firTestAttendance.getAttendanceType().toString(),firResAttendance.getAttendanceType().toString());
//        assertEquals(firTestAttendance.getTimeTable().toString(),firResAttendance.getTimeTable().toString());
//        assertEquals(firTestAttendance.getMember().toString(),firResAttendance.getMember().toString());
//
//        assertThat(secResAttendance,is(notNullValue()));
//        assertEquals(secTestAttendance.getIndex(),secResAttendance.getIndex());
//        assertEquals(secTestAttendance.getAttendanceType().toString(),secResAttendance.getAttendanceType().toString());
//        assertEquals(secTestAttendance.getTimeTable().toString(),secResAttendance.getTimeTable().toString());
//        assertEquals(secTestAttendance.getMember().toString(),secResAttendance.getMember().toString());
//
//    }
//
//    @Test
//    void bulkUpdate(){
//        //given
//        String modified = "test System";
//
//        List<Attendance> testAttendances = new ArrayList<>();
//
//        Attendance firTestAttendance = testUtils.getTestAttendance();
//        firTestAttendance.setIndex("hos!");
//        testAttendances.add(firTestAttendance);
//
//        Attendance secTestAttendance = testUtils.getTestAttendance2();
//        secTestAttendance.setIndex("sigong joah!");
//        testAttendances.add(secTestAttendance);
//
//        //when
//        attendanceRepository.bulkUpdate(testAttendances,modified);
//
//        //then
//        Attendance firResAttendance = testEntityManager.find(Attendance.class,firTestAttendance.getId());
//        Attendance secResAttendance = testEntityManager.find(Attendance.class,secTestAttendance.getId());
//
//        //내용 검증
//        assertThat(firResAttendance,is(notNullValue()));
//        assertEquals(firTestAttendance.getIndex(),firResAttendance.getIndex());
//        assertEquals(firTestAttendance.getAttendanceType().toString(),firResAttendance.getAttendanceType().toString());
//        assertEquals(firTestAttendance.getTimeTable().toString(),firResAttendance.getTimeTable().toString());
//        assertEquals(firTestAttendance.getMember().toString(),firResAttendance.getMember().toString());
//        //수정자 변경 여부
//        assertEquals(modified,firResAttendance.getModifiedBy());
//        //수정 시간 변경 여부
//        assertNotEquals(firTestAttendance.getModifiedDateTime(),firResAttendance.getModifiedDateTime());
//
//        //내용 검증
//        assertThat(secResAttendance,is(notNullValue()));
//        assertEquals(secTestAttendance.getIndex(),secResAttendance.getIndex());
//        assertEquals(secTestAttendance.getAttendanceType().toString(),secResAttendance.getAttendanceType().toString());
//        assertEquals(secTestAttendance.getTimeTable().toString(),secResAttendance.getTimeTable().toString());
//        assertEquals(secTestAttendance.getMember().toString(),secResAttendance.getMember().toString());
//        //수정자 변경 여부
//        assertEquals(modified,secResAttendance.getModifiedBy());
//        //수정 시간 변경 여부
//        assertNotEquals(secTestAttendance.getModifiedDateTime(),secResAttendance.getModifiedDateTime());
//
//    }
//
//    @Test
//    void findAll(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//        Attendance testAttendances2 = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("this is not working param",testAttendances.getMember().getId());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(2L,res.getTotalElements());
//        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
//    }
//
//    @Test
//    void findAllWithAttendanceType(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithAttendanceType2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithMember(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("memberID",testAttendances.getMember().getId());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithMember2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("memberID",testAttendances.getMember().getId());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithTimeTable(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithTimeTable2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithCreatedBy(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("createBy",testAttendances.getCreateBy());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithCreatedBy2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("createBy",testAttendances.getCreateBy());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithModifiedBy(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("modifiedBy",testAttendances.getCreateBy());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithModifiedBy2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("modifiedBy",testAttendances.getCreateBy());
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
//    @Test
//    void findAllWithCreatedDateTimeWithStartAndEnd(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
//    @Test
//    void findAllWithCreatedDateTimeWithStartAndEnd2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
//    @Test
//    void findAllWithCreatedDateTimeWithStart(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//        Attendance testAttendances2 = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(2L,res.getTotalElements());
//        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
//    @Test
//    void findAllWithCreatedDateTimeWithStart2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
//    @Test
//    void findAllWithCreatedDateTimeWithEnd(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
//    @Test
//    void findAllWithCreatedDateTimeWithEnd2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//        Attendance testAttendances2 = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(2L,res.getTotalElements());
//        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
//    @Test
//    void findAllWithModifiedDateTimeWithStartAndEnd(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
//    @Test
//    void findAllWithModifiedDateTimeWithStartAndEnd2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
//    @Test
//    void findAllWithModifiedDateTimeWithStart(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//        Attendance testAttendances2 = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(2L,res.getTotalElements());
//        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
//    @Test
//    void findAllWithModifiedDateTimeWithStart2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
//    @Test
//    void findAllWithModifiedDateTimeWithEnd(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @DisplayName("Attendance modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
//    @Test
//    void findAllWithModifiedDateTimeWithEnd2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//        Attendance testAttendances2 = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(2L,res.getTotalElements());
//        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testAttendances,testAttendances2)));
//    }
//
//    @Test
//    void findAllWithIndex(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("index","출결 코드를");
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//
//    }
//
//    @Test
//    void findAllWithIndex2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("index","이것은");
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//
//    }
//
//    @Test
//    void findAllWithIntegratedSpec(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("modifiedBy",testAttendances.getCreateBy());
//        queryParam.add("createBy",testAttendances.getCreateBy());
//        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
//        queryParam.add("memberID",testAttendances.getMember().getId());
//        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("index","출결 코드를");
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//
//    @Test
//    void findAllWithIntegratedSpec2(){
//        //given
//        Attendance testAttendances = testUtils.getTestAttendance2();
//
//        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//        queryParam.add("modifiedBy",testAttendances.getCreateBy());
//        queryParam.add("createBy",testAttendances.getCreateBy());
//        queryParam.add("timeTableID",testAttendances.getTimeTable().getId().toString());
//        queryParam.add("memberID",testAttendances.getMember().getId());
//        queryParam.add("attendanceTypeID",testAttendances.getAttendanceType().getId().toString());
//        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        queryParam.add("index","이것은");
//
//        AttendanceSpecification testSpec = attendanceSpecificationBuilder.toSpec(queryParam);
//
//        //when
//        Page<Attendance> res = attendanceRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"member")));
//
//        //then
//        assertThat(res,is(notNullValue()));
//        assertEquals(1L,res.getTotalElements());
//        assertEquals(testAttendances.toString(),res.getContent().get(0).toString());
//    }
//}