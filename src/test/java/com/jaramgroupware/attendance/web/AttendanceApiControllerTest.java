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
                        .header("user_pk",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("????????? attendance??? TimeTable(Object)??? ID"),
                                fieldWithPath("[].member_id").description("????????? attendance??? Member(Object)??? ID"),
                                fieldWithPath("[].attendance_type_id").description("?????? attendance??? AttendanceType(Object)??? ID").attributes(field("constraints", "AttendanceType (object)??? ID(PK)")),
                                fieldWithPath("[].index").description("????????? ?????? ??????").optional().attributes(field("constraints", "?????? 255???"))
                        ),
                        responseFields(
                                fieldWithPath("message").description("????????? ????????? attendance??? ????????? ???????????????.")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("??? (1)?????? Attendance??? ??????????????? ??????????????????!"));
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
        queryParam.add("index","?????? ?????????");

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
                .andDo(document("attendance-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].created_date_time").description("attendance??? ????????? ??????"),
                                fieldWithPath("[].modified_date_time").description("attendance??? ????????? ?????? ??????"),
                                fieldWithPath("[].create_by").description("?????? attendance??? ????????? ??????"),
                                fieldWithPath("[].modify_by").description("?????? attendance??? ??????????????? ????????? ??????"),
                                fieldWithPath("[].index").description("?????? attendance??? ?????? ??????").attributes(field("constraints", "?????? 255???")),

                                //AttendanceType object
                                fieldWithPath("[].attendance_type_id").description("?????? attendance??? AttendanceType(Object)??? ID"),
                                fieldWithPath("[].attendance_type_name").description("?????? attendance??? AttendanceType(Object)??? ??????"),

                                //Member object
                                fieldWithPath("[].member_id").description("?????? attendance??? ?????? Member(Object)??? ID"),
                                fieldWithPath("[].member_name").description("?????? attendance??? ?????? Member(Object)??? ??????"),


                                //TimeTable object
                                fieldWithPath("[].time_table_id").description("?????? attendance??? ?????? TimeTableID(Object)??? ID"),
                                fieldWithPath("[].time_table_name").description("?????? attendance??? ?????? TimeTableID(Object)??????")
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
        queryParam.add("index","?????? ?????????");

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
        queryParam.add("index","?????? ?????????");

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
        queryParam.add("index","?????? ?????????");

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
                        .header("user_pk",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("attendance-del",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("????????? attendance??? TimeTable(Object)??? ID"),
                                fieldWithPath("[].member_id").description("????????? attendance??? Member(Object)??? ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("????????? ????????? attendance??? ??????")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("??? (2)?????? Attendance??? ??????????????? ??????????????????!"));
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
                        .header("user_pk",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDtos))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].time_table_id").description("????????? attendance??? TimeTable(Object)??? ID"),
                                fieldWithPath("[].member_id").description("????????? attendance??? Member(Object)??? ID"),
                                fieldWithPath("[].attendance_type_id").description("?????? attendance??? AttendanceType(Object)??? ID"),
                                fieldWithPath("[].index").description("????????? ?????? ??????").optional()
                        ),
                        responseFields(
                                fieldWithPath("message").description("??????????????? ????????? attendance??? ??????")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("??? (2)?????? Attendance??? ??????????????? ???????????? ????????????!"));
        verify(attendanceService).update(anyList(),anyString());
    }
}