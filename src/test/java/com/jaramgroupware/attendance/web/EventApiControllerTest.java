package com.jaramgroupware.attendance.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.attendance.TestUtils;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.event.EventSpecification;
import com.jaramgroupware.attendance.domain.event.EventSpecificationBuilder;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doReturn;

import static com.jaramgroupware.attendance.RestDocsConfig.field;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class EventApiControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private EventSpecificationBuilder eventSpecificationBuilder;

    @MockBean
    private EventService eventService;

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
    void addEvent() throws Exception {
        //given
        EventAddRequestControllerDto testEventDto = EventAddRequestControllerDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .index(testUtils.getTestEvent().getIndex())
                .name(testUtils.getTestEvent().getName())
                .build();

        doReturn(1L).when(eventService).add(testEventDto.toServiceDto(),testUtils.getTestUid());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/event")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("event-add-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Event의 이름").attributes(field("constraints", "최소 1자, 최대 50자")),
                                fieldWithPath("index").description("Event의 설명").attributes(field("constraints", "최대 255자")).optional(),
                                fieldWithPath("start_date_time").description("Event의 시작 date time"),
                                fieldWithPath("end_date_time").description("Event의 종료 date time").attributes(field("constraints", "end date time은 start date time 보다 더 미래의 시간이여야함."))
                        ),
                        responseFields(
                                fieldWithPath("event_id").description("새롭게 추가된 event의 ID ")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.event_id").value("1"));

        verify(eventService).add(testEventDto.toServiceDto(),testUtils.getTestUid());
    }

    @Test
    void getEventById() throws Exception {
        //given
        Long eventID = 1L;

        EventResponseServiceDto eventResponseServiceDto = new EventResponseServiceDto(testUtils.getTestEvent());

        doReturn(eventResponseServiceDto).when(eventService).findById(eventID);


        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/event/{eventID}",eventID)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("event-get-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventID").description("대상 Event의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("대상 event의 ID"),
                                fieldWithPath("name").description("대상 event의 name"),
                                fieldWithPath("index").description("대상 event의 설명"),
                                fieldWithPath("start_date_time").description("대상 event의 시작 date time"),
                                fieldWithPath("end_date_time").description("대상 event의 종료 date time")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventResponseServiceDto.toControllerDto())));
        verify(eventService).findById(eventID);
    }

    @Test
    void getEventAll() throws Exception {
        //given
        List<EventResponseServiceDto> targetEventList = new ArrayList<EventResponseServiceDto>();

        EventResponseServiceDto eventResponseServiceDto = new EventResponseServiceDto(testUtils.getTestEvent());
        targetEventList.add(eventResponseServiceDto);


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

        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Direction.DESC,"id"));
        Specification<Event> spec = Mockito.mock(EventSpecification.class);
        doReturn(spec).when(eventSpecificationBuilder).toSpec(queryParam);
        doReturn(targetEventList).when(eventService).findAll(any(),any());

        //when

        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/event")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParams(queryParam))
                .andDo(print())
                .andDo(document("event-get-multiple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("대상 event의 ID"),
                                fieldWithPath("[].name").description("대상 event의 name"),
                                fieldWithPath("[].index").description("대상 event의 설명"),
                                fieldWithPath("[].start_date_time").description("대상 event의 시작 date time"),
                                fieldWithPath("[].end_date_time").description("대상 event의 종료 date time")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetEventList.stream()
                                        .map(EventResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));
        verify(eventService).findAll(any(),any());
        verify(eventSpecificationBuilder).toSpec(queryParam);
    }

    @Test
    void delEvent() throws Exception {
        //given
        Long eventID = 1L;

        doReturn(1L).when(eventService).delete(eventID);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/event/{eventID}",eventID)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("event-del-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventID").description("삭제할 event의 id")
                        ),
                        responseFields(
                                fieldWithPath("event_id").description("삭제가 완료된 event의 ID")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.event_id").value("1"));
        verify(eventService).delete(eventID);
    }

    @Test
    void updateEvent() throws Exception {
        //given
        Long eventID = 1L;

        EventUpdateRequestControllerDto eventUpdateRequestServiceDto = EventUpdateRequestControllerDto.builder()
                .startDateTime(testUtils.getTestDateTime())
                .endDateTime(testUtils.getTestDateTime())
                .index(testUtils.getTestEvent().getIndex())
                .name(testUtils.getTestEvent().getName())
                .build();

        EventResponseServiceDto testEventResult = new EventResponseServiceDto(testUtils.getTestEvent());

        doReturn(testEventResult).when(eventService).update(eventID,eventUpdateRequestServiceDto.toServiceDto(),testUtils.getTestUid());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/event/{eventID}",eventID)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUpdateRequestServiceDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("event-update-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventID").description("업데이트할 attendance의 id")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Event의 이름").attributes(field("constraints", "최소 1자, 최대 50자")),
                                fieldWithPath("index").description("Event의 설명").attributes(field("constraints", "최대 255자")).optional(),
                                fieldWithPath("start_date_time").description("Event의 시작 date time"),
                                fieldWithPath("end_date_time").description("Event의 종료 date time").attributes(field("constraints", "end date time은 start date time 보다 더 미래의 시간이여야함."))
                        ),
                        responseFields(
                                fieldWithPath("id").description("대상 event의 ID"),
                                fieldWithPath("name").description("대상 event의 name"),
                                fieldWithPath("index").description("대상 event의 설명"),
                                fieldWithPath("start_date_time").description("대상 event의 시작 date time"),
                                fieldWithPath("end_date_time").description("대상 event의 종료 date time")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testEventResult.toControllerDto())));
        verify(eventService).update(eventID,eventUpdateRequestServiceDto.toServiceDto(),testUtils.getTestUid());
    }
}