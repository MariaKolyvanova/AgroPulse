package ru.ochakovo.agropulse.mapper.report.core;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.report.core.ReportCreateDto;
import ru.ochakovo.agropulse.entity.data.Report;

@Mapper(componentModel = "spring")
public interface ReportCreateMapper {
    Report map(ReportCreateDto reportCreateDto);
}
