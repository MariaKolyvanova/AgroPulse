package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractFile extends BaseEntity {

    private String name;

    private String path;

    private String format;

    private Long size;


}