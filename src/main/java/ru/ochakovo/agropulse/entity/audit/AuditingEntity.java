package ru.ochakovo.agropulse.entity.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.ochakovo.agropulse.entity.BaseEntity;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditingEntity extends BaseEntity {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    @NotAudited
    private Instant createdDate;

    @LastModifiedDate
    @NotAudited
    private Instant modifiedDate;

    @CreatedBy
    @NotAudited
    private String createdBy;

    @LastModifiedBy
    @NotAudited
    private String modifiedBy;
}
