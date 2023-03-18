package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeResponseServiceDto;
import com.jaramgroupware.attendance.utlis.date.DefaultLocalDateTimeManger;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceCodeService {
    private final RedisTemplate<String,String> redisTemplate;
    private static final  String PREFIX = "AttendanceCode_";
    private final DefaultLocalDateTimeManger localDateTimeManger;
    /**
     * 주어진 정보를 바탕으로 신규 출결 코드를 발급함.
     * @param attendanceCodeDto 생성할 코드의 정보
     * @return 생성된 코드의 정보
     * @author 황준서(37기) hzser123@gmail.com
     */
    public AttendanceCodeResponseServiceDto createCode(AttendanceCodeAddRequestServiceDto attendanceCodeDto){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();

        var key = PREFIX + attendanceCodeDto.getTimeTableId();
        var code = valueOperations.get(key);

        if(code != null) throw new ServiceException(ServiceErrorCode.ALREADY_HAS_CODE);


        valueOperations.set(key, attendanceCodeDto.getCode());

        LocalDateTime expireAt = null;
        if(attendanceCodeDto.getExpSec() > 0){
            var now = localDateTimeManger.getNow();
            expireAt = now.plusSeconds(attendanceCodeDto.getExpSec());
            redisTemplate.expire(key, Duration.ofSeconds(Duration.between(now,expireAt).toSeconds()));

            log.info("Publish Attendance Code timetable ID -> {} code -> {} exp -> {}",attendanceCodeDto.getTimeTableId(),attendanceCodeDto.getCode(),expireAt);
        }else{
            log.info("Warning! Publish Permanent Attendance Code timetable ID -> {} code -> {}",attendanceCodeDto.getTimeTableId(),attendanceCodeDto.getCode());
        }


        return AttendanceCodeResponseServiceDto.builder()
                .timetableId(Long.parseLong(attendanceCodeDto.getTimeTableId()))
                .code(attendanceCodeDto.getCode())
                .expAt(expireAt)
                .build();
    }

    /**
     * 주어진 timeTable의 code를 삭제함.
     * @param timeTableId 코드를 삭제할 timeTable의 Id
     * @author 황준서(37기) hzser123@gmail.com
     */
    public void revokeCode(Long timeTableId){
        var key = PREFIX + timeTableId.toString();
        var result = redisTemplate.delete(key);
        if(!result) throw new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE_CODE);
    }

    /**
     * 주어진 timeTable의 code 정보를 조회함.
     * @param timeTableId 코드를 조회할 timeTable의 Id
     * @return 해당 코드의 정보
     * @author 황준서(37기) hzser123@gmail.com
     */
    public AttendanceCodeResponseServiceDto getCodeByTimeTable(Long timeTableId){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        var key = PREFIX + timeTableId;

        var code = valueOperations.get(key);
        if(code == null) throw new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE_CODE);
        var expSec = redisTemplate.getExpire(key);


        return AttendanceCodeResponseServiceDto.builder()
                .code(code)
                .expAt(localDateTimeManger.getNow().plusSeconds(expSec))
                .build();

    }
}
