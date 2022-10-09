package com.jaramgroupware.attendance.dto.role.serviceDto;

import com.jaramgroupware.attendance.domain.role.Role;
import com.jaramgroupware.attendance.dto.role.controllerDto.RoleResponseControllerDto;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseServiceDto {

    private Integer id;
    private String name;

    public RoleResponseServiceDto(Role role){
        id = role.getId();
        name = role.getName();
    }

    public Role toEntity(){
        return Role.builder()
                .id(id)
                .name(name)
                .build();
    }

    public RoleResponseControllerDto toControllerDto(){
        return RoleResponseControllerDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
