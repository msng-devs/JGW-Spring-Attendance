package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.attendance.AttendanceID;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddServiceDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceAddRequestControllerDto {

    @Positive(message = "time_table_id의 형식이 잘못되었습니다!")
    @NotNull(message = "time_table_id가 비워져있습니다!")
    private Long timeTableID;

    @Size(min=28,max = 28 ,message= "member_id의 형식이 잘못되었습니다.")
    @NotNull(message = "member_id가 비워져있습니다!")
    private String memberId;

    @Positive(message = "attendance_type_id의 형식이 잘못되었습니다!")
    @NotNull(message = "attendance_type_id가 비워져 있습니다!")
    private Integer attendanceTypeID;

    @Size(max = 255 ,message= "index에 입력 가능한 전체 글자수는 255자입니다. ")
    private String index;

    public AttendanceAddServiceDto toServiceDto(){
        return AttendanceAddServiceDto.builder()
                .index(index)
                .timeTable(TimeTable.builder()
                        .id(timeTableID)
                        .build())
                .attendanceType(AttendanceType.builder()
                        .id(attendanceTypeID)
                        .build()
                )
                .member(Member.builder()
                        .id(memberId)
                        .build())
                .build();
    }
    public AttendanceID toId(){
        return AttendanceID.builder().timeTable(
                TimeTable.builder()
                        .id(timeTableID)
                        .build())
                .member(Member.builder()
                        .id(memberId)
                        .build())
                .build();
    }
}
