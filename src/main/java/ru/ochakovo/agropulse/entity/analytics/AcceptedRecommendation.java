package ru.ochakovo.finpolicy.entity.analytics;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.BaseEntity;
import ru.ochakovo.finpolicy.entity.data.Report;
import ru.ochakovo.finpolicy.entity.dictionary.Recommendation;
import ru.ochakovo.finpolicy.entity.organization.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accepted_recommendations")
public class AcceptedRecommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id")
    private Recommendation recommendation;
}