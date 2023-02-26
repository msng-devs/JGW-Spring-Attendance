package com.jaramgroupware.attendance.domain.timeTable;

import com.jaramgroupware.attendance.domain.BaseEntity;
import com.jaramgroupware.attendance.domain.event.Event;
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
        @AttributeOverride(name = "createdDateTime",column = @Column(name = "TIMETABLE_CREATED_DTTM")),
        @AttributeOverride(name = "modifiedDateTime",column = @Column(name = "TIMETABLE_MODIFIED_DTTM")),
        @AttributeOverride(name = "createBy",column = @Column(name = "TIMETABLE_CREATED_BY",length = 30)),
        @AttributeOverride(name = "modifiedBy",column = @Column(name = "TIMETABLE_MODIFIED_BY",length = 30)),
})
@Entity(name = "TIMETABLE")
public class TimeTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIMETABLE_PK")
    private Long id;

    @Column(name = "TIMETABLE_NM",length = 50)
    private String name;

    @Column(name ="TIMETABLE_INDEX",length = 200)
    private String index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_EVENT_PK",nullable = false)
    private Event event;

    @Column(name = "TIMETABLE_START_DTTM")
    private LocalDateTime startDateTime;

    @Column(name = "TIMETABLE_END_DTTM")
    private LocalDateTime endDateTime;

    public boolean validationDateTime(){
        return (event.getStartDateTime().isAfter(startDateTime) || event.getStartDateTime().isEqual(startDateTime)) &&
                (event.getEndDateTime().isBefore(endDateTime) || event.getEndDateTime().isEqual(endDateTime));
    }

    public void update(TimeTable timeTable){

        index = timeTable.getIndex();
        name = timeTable.getName();
        startDateTime = timeTable.getStartDateTime();
        endDateTime = timeTable.getEndDateTime();
        modifiedBy = timeTable.getModifiedBy();
    }


}
