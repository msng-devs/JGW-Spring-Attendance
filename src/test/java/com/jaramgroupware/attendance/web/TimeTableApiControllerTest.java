package com.jaramgroupware.attendance.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeResponseServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceCodeService;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.service.EventService;
import com.jaramgroupware.attendance.service.TimeTableService;
import com.jaramgroupware.attendance.utlis.code.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class TimeTableApiControllerTest {


    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private TimeTableService timeTableService;

    @MockBean
    private AttendanceCodeService attendanceCodeService;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private CodeGenerator codeGenerator;

    private final TestDataUtils testUtils = new TestDataUtils();
    private final LocalDateTime testDateTime = LocalDateTime.parse("2023-08-28T21:25:23.749555800");
    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addTimeTable() throws Exception {
        //given
        var testTimeTableDto = TimeTableAddRequestControllerDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .eventID(testUtils.getTestEvent().getId())
                .index("test")
                .name("test")
                .build();
        var createdTimeTable = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());
        doReturn(createdTimeTable).when(timeTableService).createTimeTable(testTimeTableDto.toServiceDto(testUtils.getTestUid()));
        var expectedTimeTable = createdTimeTable.toControllerDto();

        //when
        ResultActions result = mvc.perform(
                post("/api/v1/timetable")
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .queryParam("addAttendance","false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTimeTableDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("대상 timetable의 이름(제목)").attributes(field("constraints", "최소 1자, 최대 50자")),
                                fieldWithPath("start_date_time").description("대상 timetable의 시작 시간"),
                                fieldWithPath("end_date_time").description("대상 timetable의 종료 시간").attributes(field("constraints", "종료 시간은 시작 시간보다 이후의 시간이여야함.")),
                                fieldWithPath("event_id").description("대상 timetable의 대상 event(object)의 id").attributes(field("constraints", "양수만 입력가능")),
                                fieldWithPath("index").description("대상 timetable의 설명").attributes(field("constraints", "최대 200자")).optional()
                        ),
                        responseFields(
                                subsectionWithPath("_links").ignored(),
                                fieldWithPath("id").description("대상 timetable의 ID"),
                                fieldWithPath("name").description("대상 timetable의 이름(제목)"),
                                fieldWithPath("index").description("대상 timetable의 설명"),
                                fieldWithPath("modified_date_time").description("대상 timetable의 마지막 수정일"),
                                fieldWithPath("start_date_time").description("대상 timetable의 시작 date time"),
                                fieldWithPath("end_date_time").description("대상 timetable의 종료 date time"),
                                fieldWithPath("modify_by").description("대상 timetable를 마지막으로 수정한 자"),
                                fieldWithPath("event_id").description("대상 timetable의 대상 event(object) ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTimeTable)));
        verify(timeTableService).createTimeTable(testTimeTableDto.toServiceDto(testUtils.getTestUid()));
    }

    @Test
    void addTimeTable2() throws Exception {
        //given
        var testTimeTableDto = TimeTableAddRequestControllerDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .eventID(testUtils.getTestEvent().getId())
                .index("test")
                .name("test")
                .build();
        var createdTimeTable = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());
        doReturn(createdTimeTable).when(timeTableService).createTimeTable(any(),any(),any(),any());
        var expectedTimeTable = createdTimeTable.toControllerDto();

        //when
        ResultActions result = mvc.perform(
                        post("/api/v1/timetable")
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .queryParam("addAttendance","true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testTimeTableDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTimeTable)));
        verify(timeTableService).createTimeTable(any(),any(),any(),any());
    }

    @Test
    void findTimeTableById() throws Exception {
        //given
        Long timeTableId = 1L;

        TimeTableResponseServiceDto eventResponseServiceDto = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());

        doReturn(eventResponseServiceDto).when(timeTableService).findServiceById(timeTableId);


        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/timetable/{timeTableId}",timeTableId)
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-get-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
                        ),
                        responseFields(
                                subsectionWithPath("_links").ignored(),
                                fieldWithPath("id").description("대상 timetable의 ID"),
                                fieldWithPath("name").description("대상 timetable의 이름(제목)"),
                                fieldWithPath("index").description("대상 timetable의 설명"),
                                fieldWithPath("modified_date_time").description("대상 timetable의 마지막 수정일"),
                                fieldWithPath("start_date_time").description("대상 timetable의 시작 date time"),
                                fieldWithPath("end_date_time").description("대상 timetable의 종료 date time"),
                                fieldWithPath("modify_by").description("대상 timetable를 마지막으로 수정한 자"),
                                fieldWithPath("event_id").description("대상 timetable의 대상 event(object) ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventResponseServiceDto.toControllerDto())));
        verify(timeTableService).findServiceById(timeTableId);
    }

    @Test
    void findTimeTableAll() throws Exception {
        //given
        List<TimeTableResponseServiceDto> targetTimeTableList = new ArrayList<TimeTableResponseServiceDto>();

        TimeTableResponseServiceDto testTimeTable = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());
        targetTimeTableList.add(testTimeTable);


        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("timeTableID",testTimeTable.getEventID().toString());
        queryParam.add("modifiedBy",testTimeTable.getModifyBy());
        queryParam.add("createBy",testTimeTable.getCreatedBy());
        queryParam.add("startDateTime",testTimeTable.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endDateTime",testTimeTable.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testTimeTable.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testTimeTable.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        doReturn(targetTimeTableList).when(timeTableService).findAll(any(),any());

        //when
        ResultActions result = mvc.perform(
                get("/api/v1/timetable")
                        .header("user_pk",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParams(queryParam))
                .andDo(print())
                .andDo(document("timetable-get-multiple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("대상 timetable의 ID"),
                                fieldWithPath("[].name").description("대상 timetable의 name"),
                                fieldWithPath("[].index").description("대상 timetable의 설명"),
                                fieldWithPath("[].modified_date_time").description("대상 timetable의 마지막 수정일"),
                                fieldWithPath("[].start_date_time").description("대상 timetable의 시작 date time"),
                                fieldWithPath("[].end_date_time").description("대상 timetable의 종료 date time"),
                                fieldWithPath("[].modify_by").description("대상 timetable를 마지막으로 수정한 자"),
                                fieldWithPath("[].event_id").description("대상 timetable의 대상 event(object)의 ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetTimeTableList.stream()
                                        .map(TimeTableResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));
        verify(timeTableService).findAll(any(),any());
    }

    @Test
    void delTimeTable() throws Exception {
        //given
        Long timeTableId = 1L;

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/timetable/{timeTableId}",timeTableId)
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-del",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("삭제할 timetable의 id")
                        ),
                        responseFields(
                                fieldWithPath("message").description("처리 결과")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
        verify(timeTableService).deleteTimeTable(timeTableId);
    }

    @Test
    void updateTimeTable() throws Exception {
        //given
        Long timeTableId = 1L;

        var timeTableUpdateRequestDto = TimeTableUpdateRequestControllerDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .name(testUtils.getTestTimeTable().getName())
                .index("test")
                .build();

        var testTimeTableResult = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());

        doReturn(testTimeTableResult).when(timeTableService).updateTimeTable(timeTableUpdateRequestDto.toServiceDto(timeTableId,testUtils.getTestUid()));

        var exceptResult = testTimeTableResult.toControllerDto();

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/timetable/{timeTableId}",timeTableId)
                        .header("user_pk",testUtils.getTestUid())
                        .header("role_pk",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timeTableUpdateRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
                        ),
                        requestFields(
                                fieldWithPath("name").description("대상 timetable의 이름(제목)").attributes(field("constraints", "최소 1자, 최대 50자")),
                                fieldWithPath("start_date_time").description("대상 timetable의 시작 시간"),
                                fieldWithPath("end_date_time").description("대상 timetable의 종료 시간").attributes(field("constraints", "종료 시간은 시작 시간보다 이후의 시간이여야함.")),
                                fieldWithPath("index").description("대상 timetable의 설명").attributes(field("constraints", "최대 200자")).optional()
                        ),
                        responseFields(
                                subsectionWithPath("_links").ignored(),
                                fieldWithPath("id").description("대상 timetable의 ID"),
                                fieldWithPath("name").description("대상 timetable의 이름(제목)"),
                                fieldWithPath("index").description("대상 timetable의 설명"),
                                fieldWithPath("modified_date_time").description("대상 timetable의 마지막 수정일"),
                                fieldWithPath("start_date_time").description("대상 timetable의 시작 date time"),
                                fieldWithPath("end_date_time").description("대상 timetable의 종료 date time"),
                                fieldWithPath("modify_by").description("대상 timetable를 마지막으로 수정한 자"),
                                fieldWithPath("event_id").description("대상 timetable의 대상 event(object) ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testTimeTableResult.toControllerDto())));
        verify(timeTableService).updateTimeTable(timeTableUpdateRequestDto.toServiceDto(timeTableId,testUtils.getTestUid()));
    }

    @Test
    void publishTimeTableAttendanceCode() throws Exception {
        //given
        var testCode = "testCode";
        var requestDto = AttendanceCodeAddRequestControllerDto.builder()
                .expSec(30L)
                .build();

        var testAttendanceCodeResponse = AttendanceCodeResponseServiceDto.builder()
                .code(testCode)
                .expAt(testDateTime)
                .build();
        doReturn(testCode).when(codeGenerator).getKey(6);
        doReturn(testAttendanceCodeResponse).when(attendanceCodeService).createCode(requestDto.toServiceDto(testCode,1L));
        var exceptResult = testAttendanceCodeResponse.toControllerDto();
        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/timetable/{timeTableId}/attendanceCode",1L)
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-attendance-code-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
                        ),
                        requestFields(
                                fieldWithPath("exp_sec").description("해당 코드의 유효시간").attributes(field("constraints", "초 단위로 설정 /  최대 2592000초 / -1을 사용하면 영구적인 코드를 발급할 수 있음 / -1 외 음수 사용 불가 "))
                        ),
                        responseFields(
                                subsectionWithPath("_links").ignored(),
                                fieldWithPath("code").description("발급된 출결 코드"),
                                fieldWithPath("time_table_id").description("출결 코드가 발급된 timetable의 id"),
                                fieldWithPath("exp_at").description("해당 코드가 만료되는 시간")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptResult)));
        verify(codeGenerator).getKey(6);
        verify(attendanceCodeService).createCode(requestDto.toServiceDto(testCode,1L));
    }

    @Test
    void registerAttendanceCode() throws Exception {
        //given
        var testCode = "testCode";
        var testCodeInfo = AttendanceCodeResponseServiceDto.builder()
                .code(testCode)
                .expAt(testDateTime)
                .build();

        var attendanceAddRequestServiceDto = AttendanceAddRequestServiceDto.builder()
                .attendanceTypeId(1)
                .createdBy("system")
                .index("출결 코드를 통해 처리된 출결 정보 입니다.")
                .memberId(testUtils.getTestUid())
                .timeTableId(1L)
                .build();

        var createdAttendance = new AttendanceResponseServiceDto(attendanceAddRequestServiceDto.toEntity(testUtils.getTestMember(),testUtils.getTestAttendanceType(),testUtils.getTestTimeTable()));
        doReturn(testCodeInfo).when(attendanceCodeService).getCodeByTimeTable(1L);
        doReturn(createdAttendance).when(attendanceService).createAttendance(attendanceAddRequestServiceDto);

        var exceptResult = createdAttendance.toControllerDto();

        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/timetable/{timeTableId}/attendanceCode/register",1L)
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .queryParam("code",testCode)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-attendance-code-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
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
                .andExpect(content().json(objectMapper.writeValueAsString(exceptResult)));
        verify(attendanceCodeService).getCodeByTimeTable(1L);
        verify(attendanceService).createAttendance(attendanceAddRequestServiceDto);
    }

    @Test
    void getTimeTableAttendanceCode() throws Exception {
        //given
        var testCode = "testCode";
        var testCodeInfo = AttendanceCodeResponseServiceDto.builder()
                .code(testCode)
                .expAt(testDateTime)
                .build();

        doReturn(testCodeInfo).when(attendanceCodeService).getCodeByTimeTable(1L);

        var exceptResult = testCodeInfo.toControllerDto();

        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/timetable/{timeTableId}/attendanceCode",1L)
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-attendance-code-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
                        ),
                        responseFields(
                                subsectionWithPath("_links").ignored(),
                                fieldWithPath("code").description("발급된 출결 코드"),
                                fieldWithPath("time_table_id").description("출결 코드가 발급된 timetable의 id"),
                                fieldWithPath("exp_at").description("해당 코드가 만료되는 시간")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptResult)));
        verify(attendanceCodeService).getCodeByTimeTable(1L);
    }

    @Test
    void deleteTimeTableAttendanceCode() throws Exception {
        //given

        //when
        ResultActions result = mvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/v1/timetable/{timeTableId}/attendanceCode",1L)
                                .header("user_pk",testUtils.getTestUid())
                                .header("role_pk",4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("timetable-attendance-code-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeTableId").description("대상 timetable의 id")
                        ),
                        responseFields(
                                fieldWithPath("message").description("처리결과")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
        verify(timeTableService).findServiceById(1L);
        verify(attendanceCodeService).revokeCode(1L);
    }
}