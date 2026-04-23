package ru.ochakovo.finpolicy.entity.organization;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.BaseEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    private String region;

    private String industry;
}