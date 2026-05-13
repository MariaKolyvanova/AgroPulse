package ru.ochakovo.agropulse.entity.organization;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.audit.AuditingEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends AuditingEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String lastName;

    private String firstName;

    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;
}