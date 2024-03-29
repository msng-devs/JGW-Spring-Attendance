package com.jaramgroupware.attendance.config;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.role.Role;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class TestDataUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LocalDate testDate;
    private final LocalDate testDate2;
    private final AttendanceType testAttendanceType;
    private final AttendanceType testAttendanceType2;
    private final Role testRole;
    private final Role testRole2;
    private final LocalDateTime testDateTime = LocalDateTime.parse("2022-08-04 04:16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final LocalDateTime testDateTime2 = LocalDateTime.parse("2022-08-28 04:16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final Member testMember;
    private final Member testMember2;
    private final Event testEvent;
    private final Event testEvent2;
    private final TimeTable testTimeTable;
    private final TimeTable testTimeTable2;
    public final Attendance testAttendance;
    public final Attendance testAttendance2;
    public final String testUid;

    public boolean isListSame(List<?> targetListA , List<?> targetListB){

        if(targetListA.size() != targetListB.size()) return false;
        for (int i = 0; i < targetListA.size(); i++) {
            try{
                targetListA.indexOf(targetListB.get(i));
            }catch (Exception e){
                logger.debug("{}",targetListA.get(i).toString());
                logger.debug("{}",targetListB.get(i).toString());
                return false;
            }
        }
        return true;
    }

    public TestDataUtils(){

        testDate = LocalDate.of(2022,1,22);
        testDate2 = LocalDate.of(2022,8,28);

        testAttendanceType = AttendanceType
                .builder()
                .id(1)
                .name("출석")
                .build();

        testAttendanceType2 = AttendanceType
                .builder()
                .id(2)
                .name("결석")
                .build();

        testRole = Role.builder()
                .id(1)
                .name("ROLE_ADMIN")
                .build();

        testRole2 = Role.builder()
                .id(2)
                .name("ROLE_DEV")
                .build();

        testMember = Member.builder()
                .id("Th1s1sNotRea1U1DDOY0UKNOWH0S")
                .name("황테스트")
                .email("hwangTest@test.com")
                .role(testRole)
                .isStatus(true)
                .build();

        testMember2 = Member.builder()
                .id("ThiS1SNotRea1U1DDOY0UKNOWHoS")
                .name("김테스트")
                .email("kimTest@test.com")
                .role(testRole2)
                .isStatus(false)
                .build();

        testEvent = Event.builder()
                .id(1L)
                .name("시공좋아")
                .index("히오스, 저희 가게 아직 정상 영업하나요")
                .startDateTime(testDateTime)
                .endDateTime(testDateTime)
                .build();

        testEvent.setModifiedBy("system");
        testEvent.setCreateBy("system");
        testEvent.setCreatedDateTime(testDateTime);
        testEvent.setModifiedDateTime(testDateTime);

        testEvent2 = Event.builder()
                .id(2L)
                .name("시공축제")
                .index("히오스, 저희 가게 아직 정상 영업합니다2")
                .startDateTime(testDateTime2)
                .endDateTime(testDateTime2)
                .build();
        testEvent2.setModifiedBy("system2");
        testEvent2.setCreateBy("system2");
        testEvent2.setCreatedDateTime(testDateTime2);
        testEvent2.setModifiedDateTime(testDateTime2);

        testTimeTable = TimeTable.builder()
                .id(1L)
                .event(testEvent)
                .name("test timetable1")
                .startDateTime(testDateTime)
                .endDateTime(testDateTime)
                .index("상세 설명이 없습니다")
                .build();

        testTimeTable.setModifiedBy("system");
        testTimeTable.setCreateBy("system");
        testTimeTable.setCreatedDateTime(testDateTime);
        testTimeTable.setModifiedDateTime(testDateTime);

        testTimeTable2 = TimeTable.builder()
                .id(2L)
                .event(testEvent2)
                .name("test timetable2")
                .startDateTime(testDateTime2)
                .endDateTime(testDateTime2)
                .index("상세 설명이 없습니다")
                .build();

        testTimeTable2.setModifiedBy("system2");
        testTimeTable2.setCreateBy("system2");
        testTimeTable2.setCreatedDateTime(testDateTime2);
        testTimeTable2.setModifiedDateTime(testDateTime2);

       testAttendance = Attendance.builder()
                .id(1L)
                .member(testMember)
                .attendanceType(testAttendanceType)
                .timeTable(testTimeTable)
                .index("출결 코드를 통해 자동적으로 출결처리가 되었습니다.")
                .build();

        testAttendance.setModifiedBy("system");
        testAttendance.setCreateBy("system");
        testAttendance.setCreatedDateTime(testDateTime);
        testAttendance.setModifiedDateTime(testDateTime);

        testAttendance2 = Attendance.builder()
                .id(2L)
                .member(testMember2)
                .attendanceType(testAttendanceType2)
                .timeTable(testTimeTable2)
                .index("이것은 테스트용 출결입니다.")
                .build();

        testAttendance2.setModifiedBy("system2");
        testAttendance2.setCreateBy("system2");
        testAttendance2.setCreatedDateTime(testDateTime2);
        testAttendance2.setModifiedDateTime(testDateTime2);

        testUid = testMember.getId();
    }
    public HttpEntity<?> createHttpEntity(Object dto,String userUid){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user_uid",userUid);

        return new HttpEntity<>(dto, headers);
    }

    public HttpEntity<?> createHttpEntity(Object dto,String userUid,Integer roleId){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user_uid",userUid);
        headers.add("role_pk",userUid);
        return new HttpEntity<>(dto, headers);
    }

    public Map<String,Object> getString(String arg, Object value) {
        return Collections.singletonMap(arg, value);
    }
}
