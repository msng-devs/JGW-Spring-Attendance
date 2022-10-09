package com.jaramgroupware.attendance.dto.rank.serviceDto;

import com.jaramgroupware.attendance.domain.rank.Rank;
import com.jaramgroupware.attendance.dto.rank.controllerDto.RankResponseControllerDto;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankResponseServiceDto {

    private Integer id;
    private String name;

    public RankResponseServiceDto(Rank rank){
        id = rank.getId();
        name = rank.getName();
    }

    public Rank toEntity(){
        return Rank.builder()
                .id(id)
                .name(name)
                .build();
    }

    public RankResponseControllerDto toControllerDto(){
        return RankResponseControllerDto.builder()
                .id(id)
                .name(name)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
