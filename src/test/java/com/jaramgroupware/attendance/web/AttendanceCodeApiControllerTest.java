package com.jaramgroupware.attendance.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.attendance.AttendanceID;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeRegisterRequestControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeServiceDto;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.service.AttendanceCodeService;
import com.jaramgroupware.attendance.service.AttendanceService;
import com.jaramgroupware.attendance.service.AttendanceTypeService;
import com.jaramgroupware.attendance.service.TimeTableService;
import com.jaramgroupware.attendance.utils.key.KeyGenerator;
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

import static com.jaramgroupware.attendance.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
class AttendanceCodeApiControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AttendanceCodeService attendanceCodeService;

    @MockBean
    private TimeTableService timeTableService;

    @MockBean
    private AttendanceTypeService attendanceTypeService;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private KeyGenerator keyGenerator;

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
    void createdAttendanceCode() throws Exception {

        //given
        AttendanceCodeAddRequestControllerDto testDto = AttendanceCodeAddRequestControllerDto.builder()
                .minute(30)
                .timeTableId(testUtils.getTestTimeTable().getId())
                .build();

        TimeTableResponseServiceDto timeTableResponseServiceDto = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());

        String resultCode = "123456";

        doReturn(timeTableResponseServiceDto).when(timeTableService).findById(testDto.getTimeTableId());
        doReturn(false).when(attendanceCodeService).checkHasKey(testDto.getTimeTableId());
        doReturn(resultCode).when(keyGenerator).getKey(6);
        doReturn(resultCode).when(attendanceCodeService).createCode(testDto.toServiceDto(resultCode));

        //when
        ResultActions result = mvc.perform(
                post("/api/v1/attendance-code")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-code-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("minute").description("Attendance Code의 유효 시간").attributes(field("constraints", "최소 1분, 최대 1440자")),
                                subsectionWithPath("time_table_id").description("Attendance Code 대상 TimeTable(Object)의 id").attributes(field("constraints", "TimeTable(Object)의 id(PK)"))
                        ),
                        responseFields(
                                fieldWithPath("code").description("생성된 Attendance Code")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(resultCode));
        verify(timeTableService).findById(testDto.getTimeTableId());
        verify(attendanceCodeService).checkHasKey(testDto.getTimeTableId());
        verify(keyGenerator).getKey(6);
        verify(attendanceCodeService).createCode(testDto.toServiceDto(resultCode));

    }

    @Test
    void revokeAttendanceCode() throws Exception {


        //given
        Long targetTimeTableId = 1L;

        doReturn(false).when(attendanceCodeService).validationKey(targetTimeTableId);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/attendance-code/{targetTimeTableId}",targetTimeTableId)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-code-revoke",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("targetTimeTableId").description("대상 TimeTable의 id")
                        ),
                        responseFields(
                                fieldWithPath("time_table_id").description("Attendance Code를 삭제한 TimeTable의 id")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.time_table_id").value(targetTimeTableId));
        verify(attendanceCodeService).validationKey(targetTimeTableId);
        verify(attendanceCodeService).revokeCode(targetTimeTableId);

    }

    @Test
    void findAttendanceCode() throws Exception {
        //given
        Long targetTimeTableId = 1L;
        AttendanceCodeServiceDto keySearchResult = AttendanceCodeServiceDto.builder()
                .code("123456")
                .minute(30)
                .timeTableId(targetTimeTableId)
                .build();

        AttendanceCodeResponseControllerDto resultDto = AttendanceCodeResponseControllerDto.builder()
                .minute(keySearchResult.getMinute())
                .timeTableId(keySearchResult.getTimeTableId())
                .code(keySearchResult.getCode())
                .build();


        doReturn(keySearchResult).when(attendanceCodeService).findKey(targetTimeTableId);

        //when

        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/attendance-code/{targetTimeTableId}",targetTimeTableId)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-code-find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("targetTimeTableId").description("대상 TimeTable의 id")
                        ),
                        responseFields(
                                fieldWithPath("code").description("해당 TimeTable의 Attendance Code"),
                                fieldWithPath("time_table_id").description("해당 TimeTable의 id"),
                                fieldWithPath("minute").description("Attendance Code의 남은 유효시간 (분)")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resultDto)));
        verify(attendanceCodeService).findKey(targetTimeTableId);
    }

    @Test
    void registerAttendanceCode() throws Exception {
        //given
        Long testAttendanceId = 1L;
        AttendanceCodeRegisterRequestControllerDto attendanceCodeRegisterRequestControllerDto
                = AttendanceCodeRegisterRequestControllerDto
                .builder()
                .code("123456")
                .timeTableID(testUtils.getTestTimeTable().getId())
                .build();

        AttendanceCodeServiceDto keySearchResult = AttendanceCodeServiceDto.builder()
                .code("123456")
                .minute(30)
                .timeTableId(testUtils.getTestTimeTable().getId())
                .build();
        TimeTableResponseServiceDto timeTableResponseServiceDto = new TimeTableResponseServiceDto(testUtils.getTestTimeTable());
        AttendanceTypeResponseServiceDto attendanceTypeResult = new AttendanceTypeResponseServiceDto(testUtils.getTestAttendanceType());



        doReturn(timeTableResponseServiceDto).when(timeTableService).findById(attendanceCodeRegisterRequestControllerDto.getTimeTableID());
        doReturn(keySearchResult).when(attendanceCodeService).findKey(testUtils.getTestTimeTable().getId());
        doReturn(attendanceTypeResult).when(attendanceTypeService).findById(1);
        doReturn(AttendanceID.
                builder()
                .member(Member.builder().id(testUtils.getTestUid()).build())
                .timeTable(TimeTable.builder().id(attendanceCodeRegisterRequestControllerDto.getTimeTableID()).build())
                .build())
                .when(attendanceService)
                .add(attendanceCodeRegisterRequestControllerDto.toAttendanceServiceDto(attendanceTypeResult.toEntity(),timeTableResponseServiceDto.toEntity(),Member.builder().id(testUtils.getTestUid()).build()),"system");

        //when
        ResultActions result = mvc.perform(
                post("/api/v1/attendance-code/register")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceCodeRegisterRequestControllerDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("attendance-code-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("code").description("Attendance Code").attributes(field("constraints", "6자")),
                                fieldWithPath("time_table_id").description("Attendance Code를 등록할 TimeTable(Object)의 ID")
                        ),
                        responseFields(
                                fieldWithPath("time_table_id").description("대상 attendance의 TimeTable(Ojbect)의 ID"),
                                fieldWithPath("member_id").description("대상 attendance의 Member(Object)의 ID")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.time_table_id").value(attendanceCodeRegisterRequestControllerDto.getTimeTableID()))
                .andExpect(jsonPath("$.member_id").value(testUtils.getTestUid()));
        verify(timeTableService).findById(attendanceCodeRegisterRequestControllerDto.getTimeTableID());
        verify(attendanceCodeService).findKey(testUtils.getTestTimeTable().getId());
        verify(attendanceTypeService).findById(1);
        verify(attendanceService).add(attendanceCodeRegisterRequestControllerDto.toAttendanceServiceDto(attendanceTypeResult.toEntity(),timeTableResponseServiceDto.toEntity(),Member.builder().id(testUtils.getTestUid()).build()),"system");
    }
}