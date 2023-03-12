package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.config.TestDataUtils;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeResponseServiceDto;
import com.jaramgroupware.attendance.utlis.date.DefaultLocalDateTimeManger;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ComponentScan
@ExtendWith(MockitoExtension.class)
class AttendanceCodeServiceTest {

    @InjectMocks
    private AttendanceCodeService attendanceCodeService;

    @Mock
    private DefaultLocalDateTimeManger localDateTimeManger;

    @Mock
    private RedisTemplate<String,String> redisTemplate;

    private final TestDataUtils testUtils = new TestDataUtils();

    private final ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
    private final LocalDateTime testDateTime = LocalDateTime.parse("2023-08-28T21:25:23.749555800");
    private static final  String PREFIX = "AttendanceCode_";

    @DisplayName("createCode - code를 가지고 있지 않은 timeTable로 코드 발급을 요청하면, 출결 코드를 발급한다.")
    @Test
    void createCode() {
        //given
        var testCode = "thisIsTestCode";

        var testRequestDto = AttendanceCodeAddRequestServiceDto
                .builder()
                .code(testCode)
                .expSec(30L)
                .timeTableId("1")
                .build();


        var testExceptResult = AttendanceCodeResponseServiceDto.builder()
                    .code(testCode)
                    .expAt(testDateTime.plus(Duration.ofSeconds(testRequestDto.getExpSec())))
                    .build();

        doReturn(valueOperations).when(redisTemplate).opsForValue();
        doReturn(null).when(valueOperations).get(PREFIX + "1");
        doReturn(testDateTime).when(localDateTimeManger).getNow();
        AttendanceCodeResponseServiceDto result = null;

        //when
        result = attendanceCodeService.createCode(testRequestDto);


        //then
        assertEquals(testExceptResult, result);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(PREFIX +"1");
        verify(valueOperations).set(PREFIX +"1", testRequestDto.getCode());
        verify(localDateTimeManger).getNow();
        verify(redisTemplate).expire(PREFIX +"1", Duration.ofSeconds(30));
    }

    @DisplayName("createCode - code를 가지고 있지 않은 timeTable로 제한시간이 0이하인 코드 발급을 요청하면, 영구적인 출결 코드를 발급한다.")
    @Test
    void createCode2() {
        //given
        var testCode = "thisIsTestCode";

        var testRequestDto = AttendanceCodeAddRequestServiceDto
                .builder()
                .code(testCode)
                .expSec(0L)
                .timeTableId("1")
                .build();

        var exceptResult = AttendanceCodeResponseServiceDto.builder()
                .code(testCode)
                .expAt(null)
                .build();
        doReturn(valueOperations).when(redisTemplate).opsForValue();
        doReturn(null).when(valueOperations).get(PREFIX +"1");
        //when
        var result = attendanceCodeService.createCode(testRequestDto);

        //then
        assertEquals(exceptResult,result);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(PREFIX +"1");
        verify(valueOperations).set(PREFIX +"1", testRequestDto.getCode());
        verifyNoMoreInteractions(redisTemplate);
    }

    @DisplayName("createCode - code를 가지고 있는 timeTable로 요청하면, ServiceException이 발생한다.")
    @Test
    void createCode3() {
        //given
        var testCode = "thisIsTestCode";

        var testRequestDto = AttendanceCodeAddRequestServiceDto
                .builder()
                .code(testCode)
                .expSec(0L)
                .timeTableId("1")
                .build();


        doReturn(valueOperations).when(redisTemplate).opsForValue();
        doReturn("test").when(valueOperations).get(PREFIX +"1");
        //when
        assertThrows(ServiceException.class,() -> attendanceCodeService.createCode(testRequestDto));
        //then
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(PREFIX +"1");
    }
    @DisplayName("redis에 키가 존재하는 time table의 code를 삭제할려고 하면, 정상적으로 삭제 처리가 된다.")
    @Test
    void revokeCode() {
        //given
        var testKey = PREFIX + "1";

        doReturn(true).when(redisTemplate).delete(testKey);
        //when
        attendanceCodeService.revokeCode(1L);
        //then
        verify(redisTemplate).delete(testKey);
    }

    @DisplayName("code가 발급되지 않은 time table의 code를 삭제 할려고 하면, ServiceException이 발생한다.")
    @Test
    void revokeCode2() {
        //given
        var testKey = PREFIX + "1";

        doReturn(false).when(redisTemplate).delete(testKey);
        //when
        assertThrows(ServiceException.class,() -> attendanceCodeService.revokeCode(1L));

        //then
        verify(redisTemplate).delete(testKey);
    }

    @DisplayName("code가 존재하는 time table로 조회하면, 정상적으로 결과를 리턴한다.")
    @Test
    void getCodeByTimeTable() {
        //given
        var testKey = PREFIX + "1";
        var testCode = "thisisTestCode";

        var exceptResult = AttendanceCodeResponseServiceDto.builder()
                .code(testCode)
                .expAt(testDateTime.plus(Duration.ofSeconds(30)))
                .build();

        doReturn(valueOperations).when(redisTemplate).opsForValue();
        doReturn(testCode).when(valueOperations).get(testKey);
        doReturn(30L).when(redisTemplate).getExpire(testKey);
        doReturn(testDateTime).when(localDateTimeManger).getNow();
        //when
        var result = attendanceCodeService.getCodeByTimeTable(1L);
        //then
        assertEquals(exceptResult,result);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(testKey);
        verify(redisTemplate).getExpire(testKey);
        verify(localDateTimeManger).getNow();
    }

    @DisplayName("code가 존재하지 않는 time table로 조회하면, ServiceException이 발생한다.")
    @Test
    void getCodeByTimeTable2() {
        //given
        var testKey = PREFIX + "1";


        doReturn(valueOperations).when(redisTemplate).opsForValue();
        doReturn(null).when(valueOperations).get(testKey);
        //when
        assertThrows(ServiceException.class,() -> attendanceCodeService.getCodeByTimeTable(1L));

        //then
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(testKey);
    }
}