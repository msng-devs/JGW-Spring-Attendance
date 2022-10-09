package com.jaramgroupware.attendance.domain.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    public void update(Event newEvent,String who){
        name = newEvent.getName();
        index = newEvent.getIndex();
        modifiedBy = who;
    }


}

