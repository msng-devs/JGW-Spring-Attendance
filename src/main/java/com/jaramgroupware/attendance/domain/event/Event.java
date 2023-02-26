package com.jaramgroupware.attendance.domain.event;

import com.jaramgroupware.attendance.domain.BaseEntity;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableDateTimes;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdDateTime",column = @Column(name = "EVENT_CREATED_DTTM")),
        @AttributeOverride(name = "modifiedDateTime",column = @Column(name = "EVENT_MODIFIED_DTTM")),
        @AttributeOverride(name = "createBy",column = @Column(name = "EVENT_CREATED_BY",length = 30)),
        @AttributeOverride(name = "modifiedBy",column = @Column(name = "EVENT_MODIFIED_BY",length = 30)),
})
@Entity(name = "EVENT")
public class Event extends BaseEntity {

    @Id
    @Column(name = "EVENT_PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_NM",nullable = false,length = 50)
    private String name;

    @Column(name = "EVENT_INDEX")
    private String index;

    @Column(name = "EVENT_START_DTTM")
    private LocalDateTime startDateTime;

    @Column(name = "EVENT_END_DTTM")
    private LocalDateTime endDateTime;

    public void update(Event newEvent){
        name = newEvent.getName();
        index = newEvent.getIndex();
        modifiedBy = newEvent.getModifiedBy();
        startDateTime = newEvent.getStartDateTime();
        endDateTime = newEvent.getEndDateTime();
    }

    public boolean validationDateTime(TimeTableDateTimes timeTableDateTimes){
        //만약 하나라도 null 일 경우 event 아래의 timetable이 한개도 없는 것으로 판단함.
        if(timeTableDateTimes.getMaxEndDateTime() == null) return true;

        return (startDateTime.isAfter(timeTableDateTimes.getMinStartDateTime()) || startDateTime.isEqual(timeTableDateTimes.getMinStartDateTime())) &&
                (endDateTime.isBefore(timeTableDateTimes.getMaxEndDateTime()) || endDateTime.isEqual(timeTableDateTimes.getMaxEndDateTime()));
    }

}

