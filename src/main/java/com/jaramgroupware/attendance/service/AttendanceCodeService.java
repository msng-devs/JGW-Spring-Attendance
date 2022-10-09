package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AttendanceCodeService {

    @Autowired
    private final RedisTemplate<Long,String> redisTemplate;

    @Transactional
    public String createCode(AttendanceCodeServiceDto attendanceCodeDto){

        ValueOperations<Long,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(attendanceCodeDto.getTimeTableId(),attendanceCodeDto.getCode());
        redisTemplate.expire(attendanceCodeDto.getTimeTableId(), Duration.ofMinutes(attendanceCodeDto.getMinute()));
        return attendanceCodeDto.getCode();
    }


    @Transactional
    public void revokeCode(Long timeTableId){
        redisTemplate.delete(timeTableId);
    }

    @Transactional(readOnly = true)
    public boolean validationKey(Long timeTableId){

        ValueOperations<Long,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(timeTableId) == null;

    }

    @Transactional(readOnly = true)
    public AttendanceCodeServiceDto findKey(Long timeTableId){

        ValueOperations<Long,String> valueOperations = redisTemplate.opsForValue();

        String code = valueOperations.get(timeTableId);

        if(code == null) throw new CustomException(ErrorCode.INVALID_TIMETABLE_ID);

        return AttendanceCodeServiceDto.builder()
                .code(code)
                .timeTableId(timeTableId)
                .minute(redisTemplate.getExpire(timeTableId).intValue())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean checkHasKey(Long timeTableId){

        ValueOperations<Long,String> valueOperations = redisTemplate.opsForValue();

        String key = valueOperations.get(timeTableId);
        return key != null;
    }

}
