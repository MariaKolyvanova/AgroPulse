package ru.ochakovo.agropulse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.audit.ReportAuditReadDto;
import ru.ochakovo.agropulse.entity.audit.ReportAudit;
import ru.ochakovo.agropulse.entity.organization.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ReportAuditReadMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "report.id", target = "reportId")
    @Mapping(source = "report.name", target = "reportName")
    @Mapping(target = "fullName", expression = "java(buildFullName(reportAudit.getUser()))")
    ReportAuditReadDto map(ReportAudit reportAudit);


    default String buildFullName(User user) {
        return user.getLastName() + " " +
                user.getFirstName() + " " +
                user.getPatronymic();
    }

    default Instant map(LocalDateTime value) {
        return value == null
                ? null
                : value.atZone(ZoneId.systemDefault()).toInstant();
    }
}
