package ru.ochakovo.agropulse.mapper.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.report.ReportReadDto;
import ru.ochakovo.agropulse.entity.data.Report;

@Mapper(componentModel = "spring")
public interface ReportReadMapper {
    @Mapping(source = "company.id", target = "companyId")
    ReportReadDto map(Report report);
}
