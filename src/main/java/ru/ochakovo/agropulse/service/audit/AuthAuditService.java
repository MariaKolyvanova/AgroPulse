package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.audit.AuthAuditFilter;
import ru.ochakovo.agropulse.dto.audit.AuthAuditReadDto;
import ru.ochakovo.agropulse.entity.audit.AuthAudit;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.audit.AuthAuditReadMapper;
import ru.ochakovo.agropulse.querydsl.QPredicates;
import ru.ochakovo.agropulse.repository.AuthAuditRepository;

import java.time.LocalDateTime;

import static ru.ochakovo.agropulse.entity.audit.QAuthAudit.authAudit;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthAuditService {

    private final AuthAuditRepository authAuditRepository;
    private final AuthAuditReadMapper authAuditReadMapper;

    public void save(Company company, User user, String ip) {
        authAuditRepository.save(
                AuthAudit.builder()
                        .company(company)
                        .user(user)
                        .authAt(LocalDateTime.now())
                        .ip(ip)
                        .build()
        );
    }

    public Page<AuthAuditReadDto> findAll(AuthAuditFilter filter, Pageable pageable) {
        var normalizedIp = StringUtils.hasText(filter.ip()) ? filter.ip().trim() : null;
        var normalizedFullName = StringUtils.hasText(filter.fullName()) ? filter.fullName().trim() : null;

        var predicate = QPredicates.builder()
                .add(normalizedIp, authAudit.ip::containsIgnoreCase)
                .add(filter.from(), from ->
                        authAudit.authAt.goe(from.atStartOfDay()))
                .add(filter.to(), to ->
                        authAudit.authAt.lt(to.plusDays(1).atStartOfDay()))
                .add(normalizedFullName, fullName ->
                        authAudit.user.lastName
                                .concat(" ")
                                .concat(authAudit.user.firstName)
                                .concat(" ")
                                .concat(authAudit.user.patronymic.coalesce(""))
                                .containsIgnoreCase(fullName)
                    )
                .build();

        return authAuditRepository.findAll(predicate, pageable).map(authAuditReadMapper::map);
    }
}
