package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.ochakovo.agropulse.entity.BaseEntity;
import ru.ochakovo.agropulse.entity.data.Report;

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
