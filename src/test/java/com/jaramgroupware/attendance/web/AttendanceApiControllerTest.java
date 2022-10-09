package com.jaramgroupware.attendance.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecificationBuilder;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceBulkUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceDeleteRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.service.AttendanceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jaramgroupware.attendance.RestDocsConfig.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.xmlunit.util.Convert.toDocument;

@Slf4j
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AttendanceApiControllerTest {


    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private AttendanceSpecificationBuilder attendanceSpecificationBuilder;

    @MockBean
    private AttendanceTypeService attendanceTypeService;


    private final TestUtils testUtils = new TestUtils();


    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addAttendance() throws Exception {

        //given
        AttendanceAddRequestControllerDto testAttendanceDto = AttendanceAddRequestControllerDto.builder()
                .memberId(testUtils.getTestMember().getId())
                .attendanceTypeID(testUtils.getTestAttendanceType().getId())
                .timeTableID(testUtils.getTestTimeTable().getId())
                .index("this is test attendance")
                .build();

        Set<AttendanceAddRequestControllerDto> testDto = new HashSet<>();
        testDto.add(testAttendanceDto);

        logger.info("{}",objectMapper.writeValueAsString(testDto));
        //when
        ResultActions result = mvc.perform(
                post("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("삭제할 attendance의 TimeTable(Object)의 ID"),
                                fieldWithPath("[].member_id").description("삭제할 attendance의 Member(Object)의 ID"),
                                fieldWithPath("[].attendance_type_id").description("대상 attendance의 AttendanceType(Object)의 ID").attributes(field("constraints", "AttendanceType (object)의 ID(PK)")),
                                fieldWithPath("[].index").description("출결에 관한 설명").optional().attributes(field("constraints", "최대 255자"))
                        ),
                        responseFields(
                                fieldWithPath("message").description("새롭게 추가된 attendance의 갯수를 리턴합니다.")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (1)개의 Attendance를 성공적으로 추가했습니다!"));
    }

    @Test
    void getAttendanceAllWithAdminNotSelf() throws Exception {

        //given
        List<AttendanceResponseServiceDto> targetAttendanceList = new ArrayList<AttendanceResponseServiceDto>();

        AttendanceResponseServiceDto testAttendances =  new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        targetAttendanceList.add(testAttendances);


        AttendanceSpecification spec = Mockito.mock(AttendanceSpecification.class);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMemberID());
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","출결 코드를");

        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Direction.DESC,"id"));
        doReturn(spec).when(attendanceSpecificationBuilder).toSpec(queryParam);

        doReturn(targetAttendanceList).when(attendanceService).findAll(any(),any());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestMember2().getId())
                        .header("user_role_id",4)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].created_date_time").description("attendance가 생성된 일자"),
                                fieldWithPath("[].modified_date_time").description("attendance의 마지막 수정 일자"),
                                fieldWithPath("[].create_by").description("해당 attendance를 추가한 대상"),
                                fieldWithPath("[].modify_by").description("해당 attendance를 마지막으로 수정한 대상"),
                                fieldWithPath("[].index").description("해당 attendance에 대한 설명").attributes(field("constraints", "최대 255자")),

                                //AttendanceType object
                                fieldWithPath("[].attendance_type_id").description("해당 attendance의 AttendanceType(Object)의 ID"),
                                fieldWithPath("[].attendance_type_name").description("해당 attendance의 AttendanceType(Object)의 명칭"),

                                //Member object
                                fieldWithPath("[].member_id").description("해당 attendance의 대상 Member(Object)의 ID"),
                                fieldWithPath("[].member_name").description("해당 attendance의 대상 Member(Object)의 실명"),


                                //TimeTable object
                                fieldWithPath("[].time_table_id").description("해당 attendance의 대상 TimeTableID(Object)의 ID"),
                                fieldWithPath("[].time_table_name").description("해당 attendance의 대상 TimeTableID(Object)이름")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetAttendanceList.stream()
                                        .map(AttendanceResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));


    }
    @Test
    void getAttendanceAllWithAdminSelf() throws Exception {

        //given
        List<AttendanceResponseServiceDto> targetAttendanceList = new ArrayList<AttendanceResponseServiceDto>();

        AttendanceResponseServiceDto testAttendances =  new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        targetAttendanceList.add(testAttendances);


        AttendanceSpecification spec = Mockito.mock(AttendanceSpecification.class);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMemberID());
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","출결 코드를");

        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Direction.DESC,"id"));
        doReturn(spec).when(attendanceSpecificationBuilder).toSpec(queryParam);

        doReturn(targetAttendanceList).when(attendanceService).findAll(any(),any());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",4)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetAttendanceList.stream()
                                        .map(AttendanceResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));


    }
    @Test
    void getAttendanceAllWithNoAdminAuthError() throws Exception {

        //given
        List<AttendanceResponseServiceDto> targetAttendanceList = new ArrayList<AttendanceResponseServiceDto>();

        AttendanceResponseServiceDto testAttendances =  new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        targetAttendanceList.add(testAttendances);


        AttendanceSpecification spec = Mockito.mock(AttendanceSpecification.class);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMemberID());
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","출결 코드를");

        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Direction.DESC,"id"));
        doReturn(spec).when(attendanceSpecificationBuilder).toSpec(queryParam);

        doReturn(targetAttendanceList).when(attendanceService).findAll(any(),any());
        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestMember2().getId())
                        .header("user_role_id",3)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void getAttendanceAllWithNoAdminNoAuthError() throws Exception {

        //given
        List<AttendanceResponseServiceDto> targetAttendanceList = new ArrayList<AttendanceResponseServiceDto>();

        AttendanceResponseServiceDto testAttendances =  new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        targetAttendanceList.add(testAttendances);


        AttendanceSpecification spec = Mockito.mock(AttendanceSpecification.class);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("memberID",testAttendances.getMemberID());
        queryParam.add("modifiedBy",testAttendances.getCreateBy());
        queryParam.add("createBy",testAttendances.getCreateBy());
        queryParam.add("startCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testAttendances.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testAttendances.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("index","출결 코드를");

        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Direction.DESC,"id"));
        doReturn(spec).when(attendanceSpecificationBuilder).toSpec(queryParam);

        doReturn(targetAttendanceList).when(attendanceService).findAll(any(),any());
        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",3)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetAttendanceList.stream()
                                        .map(AttendanceResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));

    }

    @Test
    void delAttendance() throws Exception {
        //given
        Set<AttendanceDeleteRequestControllerDto> requestDto = new HashSet<>();

        requestDto.add(AttendanceDeleteRequestControllerDto
                .builder()
                .memberId(testUtils.getTestMember().getId())
                .timeTableID(testUtils.getTestTimeTable().getId())
                .build());

        requestDto.add(AttendanceDeleteRequestControllerDto
                .builder()
                .memberId(testUtils.getTestMember2().getId())
                .timeTableID(testUtils.getTestTimeTable2().getId())
                .build());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("attendance-del",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("삭제할 attendance의 TimeTable(Object)의 ID"),
                                fieldWithPath("[].member_id").description("삭제할 attendance의 Member(Object)의 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("삭제가 완료된 attendance의 갯수")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (2)개의 Attendance를 성공적으로 삭제했습니다!"));
        verify(attendanceService).delete(anySet());
    }

    @Test
    void updateAttendance() throws Exception {


        AttendanceBulkUpdateRequestControllerDto testAttendanceDto = AttendanceBulkUpdateRequestControllerDto.builder()
                .timeTableID(testUtils.getTestAttendance().getTimeTable().getId())
                .memberId(testUtils.getTestAttendance().getMember().getId())
                .index("test")
                .attendanceTypeID(testUtils.getTestAttendance().getAttendanceType().getId())
                .build();

        AttendanceBulkUpdateRequestControllerDto testAttendanceDto2 = AttendanceBulkUpdateRequestControllerDto.builder()
                .timeTableID(testUtils.getTestAttendance2().getTimeTable().getId())
                .memberId(testUtils.getTestAttendance2().getMember().getId())
                .index("test")
                .attendanceTypeID(testUtils.getTestAttendance2().getAttendanceType().getId())
                .build();

        Set<AttendanceBulkUpdateRequestControllerDto> testDtos = new HashSet<>();
        testDtos.add(testAttendanceDto);
        testDtos.add(testAttendanceDto2);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/attendance")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDtos))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("삭제할 attendance의 TimeTable(Object)의 ID"),
                                fieldWithPath("[].member_id").description("삭제할 attendance의 Member(Object)의 ID"),
                                fieldWithPath("[].attendance_type_id").description("대상 attendance의 AttendanceType(Object)의 ID"),
                                fieldWithPath("[].index").description("출결에 관한 설명").optional()
                        ),
                        responseFields(
                                fieldWithPath("message").description("업데이트가 완료된 attendance의 갯수")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (2)개의 Attendance를 성공적으로 업데이트 했습니다!"));
        verify(attendanceService).update(anyList(),anyString());
    }
}