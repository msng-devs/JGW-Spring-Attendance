package com.jaramgroupware.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdDateTime;

    @LastModifiedDate
    protected LocalDateTime modifiedDateTime;

    protected String createBy;

    protected String modifiedBy;
}
