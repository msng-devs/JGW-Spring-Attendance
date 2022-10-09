package com.jaramgroupware.attendance.dto.attendanceCode.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddServiceDto;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceCodeRegisterRequestControllerDto {

    @NotNull(message = "Target TimeTableID가 비워져있습니다!")
    private Long timeTableID;

    //TODO 하드코딩 고치기
    @NotEmpty(message = "코드가 비워져있습니다!")
    @Size(max = 6,min = 6,message = "코드의 길이가 다릅니다!")
    private String code;


    public AttendanceAddServiceDto toAttendanceServiceDto(AttendanceType attendanceType,TimeTable timeTable,Member member){
        return AttendanceAddServiceDto.builder()
                .member(member)
                .timeTable(timeTable)
                .attendanceType(attendanceType)
                .index("출결 코드를 통해 자동적으로 출결처리가 되었습니다.")
                .build();
    }
}
