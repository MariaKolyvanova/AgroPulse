package ru.ochakovo.finpolicy.entity.analytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.ochakovo.finpolicy.entity.BaseEntity;
import ru.ochakovo.finpolicy.entity.data.Report;

@Entity
@Table(name = "report_comments")
@Getter
@Setter
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
