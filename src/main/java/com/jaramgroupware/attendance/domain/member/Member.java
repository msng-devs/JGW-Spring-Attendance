package com.jaramgroupware.attendance.domain.member;


import com.jaramgroupware.attendance.domain.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;



@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "MEMBER")
public class Member{

    @Id
    @Column(name = "MEMBER_PK",length = 28)
    private String id;

    @Email
    @Column(name = "MEMBER_EMAIL",nullable = false,length = 255)
    private String email;

    @Column(name = "MEMBER_NM",nullable = false,length = 45)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ROLE_PK",nullable = false)
    private Role role;

    @Column(name = "MEMBER_STATUS",nullable = false)
    private boolean isStatus;

}
