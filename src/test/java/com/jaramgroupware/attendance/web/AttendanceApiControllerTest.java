package com.jaramgroupware.attendance.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecification;
import com.jaramgroupware.attendance.domain.attendance.AttendanceSpecificationBuilder;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceUpdateRequestControllerDto;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static com.jaramgroupware.attendance.config.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


    private final TestDataUtils testUtils = new TestDataUtils();


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
        var testAttendanceDto = AttendanceAddRequestControllerDto.builder()
                .memberId(testUtils.getTestMember().getId())
                .attendanceTypeID(testUtils.getTestAttendanceType().getId())
                .timeTableID(testUtils.getTestTimeTable().getId())
                .index("this is test attendance")
                .build();
        var createResult = new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        var exceptResult = createResult.toControllerDto();
        doReturn(createResult).when(attendanceService).createAttendance(testAttendanceDto.toServiceDto(testUtils.getTestUid()));
        //when
        ResultActions result = mvc.perform(
                post("/api/v1/attendance")
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAttendanceDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("time_table_id").description("추가할 attendance의 time table의 ID 입니다.").attributes(field("constraints", "양수")),
                                fieldWithPath("member_id").description("추가할 attendance의 Member 의 ID 입니다.").attributes(field("constraints", "빈 문자열 x / 28자")),
                                fieldWithPath("attendance_type_id").description("추가할 attendance의 AttendanceType의 ID").attributes(field("constraints", "양수")),
                                fieldWithPath("index").description("추가할 attendance에 관한 설명").optional().attributes(field("constraints", "최대 255자"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("attendance의 id"),
                                fieldWithPath("modified_date_time").description("attendance의 마지막 수정 일자"),
                                fieldWithPath("modify_by").description("해당 attendance를 마지막으로 수정한 대상"),
                                fieldWithPath("index").description("해당 attendance에 대한 설명"),

                                //AttendanceType object
                                fieldWithPath("attendance_type_id").description("해당 attendance의 AttendanceType의 ID"),

                                //Member object
                                fieldWithPath("member_id").description("해당 attendance의 대상 Member(Object)의 ID"),

                                //TimeTable object
                                fieldWithPath("time_table_id").description("해당 attendance의 대상 TimeTableID의 ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.modified_date_time").value("2022-08-04T04:16:00"))
                .andExpect(jsonPath("$.modify_by").value(exceptResult.getModifyBy()))
                .andExpect(jsonPath("$.index").value(exceptResult.getIndex()))
                .andExpect(jsonPath("$.attendance_type_id").value(exceptResult.getAttendanceTypeID()))
                .andExpect(jsonPath("$.member_id").value(exceptResult.getMemberID()))
                .andExpect(jsonPath("$.time_table_id").value(exceptResult.getTimeTableID()));
    }

    @Test
    void getAttendance() throws Exception {

        //given
        var getResult = new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        var exceptResult = getResult.toControllerDto();
        doReturn(getResult).when(attendanceService).findAttendanceById(getResult.getId());
        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance/{attendanceId}",getResult.getId())
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("attendanceId").description("대상 attendacne의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("attendance의 id"),
                                fieldWithPath("modified_date_time").description("attendance의 마지막 수정 일자"),
                                fieldWithPath("modify_by").description("해당 attendance를 마지막으로 수정한 대상"),
                                fieldWithPath("index").description("해당 attendance에 대한 설명"),

                                //AttendanceType object
                                fieldWithPath("attendance_type_id").description("해당 attendance의 AttendanceType의 ID"),

                                //Member object
                                fieldWithPath("member_id").description("해당 attendance의 대상 Member(Object)의 ID"),

                                //TimeTable object
                                fieldWithPath("time_table_id").description("해당 attendance의 대상 TimeTableID의 ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.modified_date_time").value("2022-08-04T04:16:00"))
                .andExpect(jsonPath("$.modify_by").value(exceptResult.getModifyBy()))
                .andExpect(jsonPath("$.index").value(exceptResult.getIndex()))
                .andExpect(jsonPath("$.attendance_type_id").value(exceptResult.getAttendanceTypeID()))
                .andExpect(jsonPath("$.member_id").value(exceptResult.getMemberID()))
                .andExpect(jsonPath("$.time_table_id").value(exceptResult.getTimeTableID()));
    }

    @Test
    void getAttendance2NoAdminNotSelf() throws Exception {

        //given
        var getResult = new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        var exceptResult = getResult.toControllerDto();
        doReturn(getResult).when(attendanceService).findAttendanceById(getResult.getId());
        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/attendance/{attendanceId}",getResult.getId())
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",3)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.modified_date_time").value("2022-08-04T04:16:00"))
                .andExpect(jsonPath("$.modify_by").value(exceptResult.getModifyBy()))
                .andExpect(jsonPath("$.index").value(exceptResult.getIndex()))
                .andExpect(jsonPath("$.attendance_type_id").value(exceptResult.getAttendanceTypeID()))
                .andExpect(jsonPath("$.member_id").value(exceptResult.getMemberID()))
                .andExpect(jsonPath("$.time_table_id").value(exceptResult.getTimeTableID()));
    }

    @Test
    void getAttendanceAdminNotSelf() throws Exception {

        //given
        var getResult = new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        var exceptResult = getResult.toControllerDto();
        doReturn(getResult).when(attendanceService).findAttendanceById(getResult.getId());
        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/attendance/{attendanceId}",getResult.getId())
                                .header("user_pk",testUtils.getTestMember2().getId())
                                .header("role_pk",4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.modified_date_time").value("2022-08-04T04:16:00"))
                .andExpect(jsonPath("$.modify_by").value(exceptResult.getModifyBy()))
                .andExpect(jsonPath("$.index").value(exceptResult.getIndex()))
                .andExpect(jsonPath("$.attendance_type_id").value(exceptResult.getAttendanceTypeID()))
                .andExpect(jsonPath("$.member_id").value(exceptResult.getMemberID()))
                .andExpect(jsonPath("$.time_table_id").value(exceptResult.getTimeTableID()));
    }

    @Test
    void getAttendanceNoAdminSelf() throws Exception {

        //given
        var getResult = new AttendanceResponseServiceDto(testUtils.getTestAttendance());
        var exceptResult = getResult.toControllerDto();
        doReturn(getResult).when(attendanceService).findAttendanceById(getResult.getId());
        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/attendance/{attendanceId}",getResult.getId())
                                .header("user_pk",testUtils.getTestMember2().getId())
                                .header("role_pk",3)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
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
                        .header("user_pk",testUtils.getTestMember2().getId())
                        .header("role_pk",4)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("attendance의 id"),
                                fieldWithPath("[].modified_date_time").description("attendance의 마지막 수정 일자"),
                                fieldWithPath("[].modify_by").description("해당 attendance를 마지막으로 수정한 대상"),
                                fieldWithPath("[].index").description("해당 attendance에 대한 설명"),

                                //AttendanceType object
                                fieldWithPath("[].attendance_type_id").description("해당 attendance의 AttendanceType의 ID"),

                                //Member object
                                fieldWithPath("[].member_id").description("해당 attendance의 대상 Member(Object)의 ID"),

                                //TimeTable object
                                fieldWithPath("[].time_table_id").description("해당 attendance의 대상 TimeTableID의 ID")
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
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
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
                        .header("user_pk",testUtils.getTestMember2().getId())
                        .header("role_pk",3)
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
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",3)
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
        var testAttendanceId = 1L;

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/attendance/{attendacneId}",testAttendanceId)
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-del",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("attendacneId").description("대상 attendacne의 id")
                        )
                ));

        //then
        result.andExpect(status().isOk());
        verify(attendanceService).deleteAttendance(testAttendanceId);
    }

    @Test
    void delAttendanceMoAdmin() throws Exception {
        //given
        var testAttendanceId = 1L;

        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/v1/attendance/{attendacneId}",testAttendanceId)
                                .header("user_pk",testUtils.getTestUid())
                                .header("user_pk",2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void updateAttendance() throws Exception {

        //given
        var testAttendance = testUtils.getTestAttendance();
        var testAttendanceUpdateRequest = AttendanceUpdateRequestControllerDto.builder()
                .attendanceTypeID(Math.toIntExact(testUtils.getTestAttendance().getId()))
                .index("변경할 내용")
                .build();
        var updateResult = new AttendanceResponseServiceDto(testAttendance);
        doReturn(updateResult).when(attendanceService).updateAttendance(testAttendanceUpdateRequest.toServiceDto("test",1L));
        var exceptResult = updateResult.toControllerDto();
        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/attendance/{attendanceId}",testAttendance.getId())
                        .header("user_pk","test")
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAttendanceUpdateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("attendanceId").description("대상 attendacne의 id")
                        ),
                        requestFields(
                                fieldWithPath("attendance_type_id").description("업데이트 할 attendance의 AttendanceType의 ID").attributes(field("constraints", "양수")),
                                fieldWithPath("index").description("업데이트 할 attendance에 관한 설명").optional().attributes(field("constraints", "최대 255자"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("attendance의 id"),
                                fieldWithPath("modified_date_time").description("attendance의 마지막 수정 일자"),
                                fieldWithPath("modify_by").description("해당 attendance를 마지막으로 수정한 대상"),
                                fieldWithPath("index").description("해당 attendance에 대한 설명"),

                                //AttendanceType object
                                fieldWithPath("attendance_type_id").description("해당 attendance의 AttendanceType의 ID"),

                                //Member object
                                fieldWithPath("member_id").description("해당 attendance의 대상 Member(Object)의 ID"),

                                //TimeTable object
                                fieldWithPath("time_table_id").description("해당 attendance의 대상 TimeTableID의 ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.modified_date_time").value("2022-08-04T04:16:00"))
                .andExpect(jsonPath("$.modify_by").value(exceptResult.getModifyBy()))
                .andExpect(jsonPath("$.index").value(exceptResult.getIndex()))
                .andExpect(jsonPath("$.attendance_type_id").value(exceptResult.getAttendanceTypeID()))
                .andExpect(jsonPath("$.member_id").value(exceptResult.getMemberID()))
                .andExpect(jsonPath("$.time_table_id").value(exceptResult.getTimeTableID()));
        verify(attendanceService).updateAttendance(testAttendanceUpdateRequest.toServiceDto("test",1L));
    }

    @Test
    void updateAttendanceNoAdmin() throws Exception {

        //given
        var testAttendance = testUtils.getTestAttendance();
        var testAttendanceUpdateRequest = AttendanceUpdateRequestControllerDto.builder()
                .attendanceTypeID(Math.toIntExact(testUtils.getTestAttendance().getId()))
                .index("변경할 내용")
                .build();

        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.put("/api/v1/attendance/{attendanceId}",testAttendance.getId())
                                .header("user_pk","test")
                                .header("role_pk",1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testAttendanceUpdateRequest))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
    }
}