package ru.ochakovo.agropulse.entity.organization;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import ru.ochakovo.agropulse.entity.audit.AuditingEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Audited
@Table(name = "users")
public class User extends AuditingEntity {

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private String username;

    @NotAudited
    private String password;

    private String lastName;

    private String firstName;

    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;
}