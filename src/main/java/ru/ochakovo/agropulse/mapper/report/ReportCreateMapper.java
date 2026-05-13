package ru.ochakovo.agropulse.mapper.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.report.ReportCreateDto;
import ru.ochakovo.agropulse.entity.data.Report;

@Mapper(componentModel = "spring")
public interface ReportCreateMapper {
    @Mapping(target = "reportMonth", expression = "java((short) reportCreateDto.reportMonth().getNumber())")
    Report map(ReportCreateDto reportCreateDto);
}
