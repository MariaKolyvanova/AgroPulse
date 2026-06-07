package ru.ochakovo.agropulse.service.report.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ochakovo.agropulse.dto.report.data.IndicatorsReportsReadDto;
import ru.ochakovo.agropulse.mapper.report.data.IndicatorsReportsReadMapper;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ValuesIndicatorsFinancialReportsService {

    private final ValuesIndicatorsFinancialReportsRepository valuesIndicatorsFinancialReportsRepository;
    private final IndicatorsReportsReadMapper indicatorsReportsReadMapper;

    public List<IndicatorsReportsReadDto> findAllById(Long reportId) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return valuesIndicatorsFinancialReportsRepository.findAllByReportIdAndCompanyId(reportId, companyId).stream()
                .map(indicatorsReportsReadMapper::map).toList();

    }

}
