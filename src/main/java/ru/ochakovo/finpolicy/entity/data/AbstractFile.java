package ru.ochakovo.finpolicy.entity.data;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ochakovo.finpolicy.entity.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractFile extends BaseEntity {

    private String name;

    private String path;

    private String format;

    private Long size;

    public enum ReportStatus {
        UPLOADED,
        VALIDATING,
        FAILED,
        EDITING,
        VALIDATED,
        READY,
        CALCULATION,
        COMPLETED,
        REVIEW,
        APPROVED
    }
}