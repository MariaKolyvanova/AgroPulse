package ru.ochakovo.agropulse.service.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.ochakovo.agropulse.dto.audit.UserAuditFilter;
import ru.ochakovo.agropulse.dto.audit.UserAuditReadDto;
import ru.ochakovo.agropulse.mapper.audit.UserAuditReadMapper;
import ru.ochakovo.agropulse.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuditService {

    private final UserRepository userRepository;
    private final UserAuditReadMapper userAuditReadMapper;

    public Page<UserAuditReadDto> findAll(UserAuditFilter filter, Pageable pageable) {
        var modifiedBy = StringUtils.hasText(filter.modifiedByFullName()) ? filter.modifiedByFullName().trim() : null;
        var modifiedUser =
                StringUtils.hasText(filter.modifiedUserFullName()) ? filter.modifiedUserFullName().trim() : null;
        return userRepository.findUserAudits(modifiedBy, modifiedUser, filter.from(), filter.to(), pageable)
                .map(userAuditReadMapper::map);
    }

}
