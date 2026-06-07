package ru.ochakovo.agropulse.mapper.report.data;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.report.data.IndicatorsReportsReadDto;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialReports;

@Mapper(componentModel = "spring")

public interface IndicatorsReportsReadMapper {
    @Mapping(source = "indicatorsFinancialReports.indicatorName", target = "name")
    @Mapping(source = "value", target = "currentValue")
    IndicatorsReportsReadDto map(ValuesIndicatorsFinancialReports indicator);
}