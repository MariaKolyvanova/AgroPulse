package ru.ochakovo.agropulse.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.dto.user.UserUpdateDto;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.mapper.user.UserCreateMapper;
import ru.ochakovo.agropulse.mapper.user.UserReadMapper;
import ru.ochakovo.agropulse.mapper.user.UserUpdateMapper;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.organization.UserRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;
    private final UserUpdateMapper userUpdateMapper;
    private final CompanyRepository companyRepository;

    public List<UserReadDto> findAll(Long companyId){
        return userRepository.findAllByCompanyId(companyId).stream()
                .map(userReadMapper::map)
                .toList();
    }

    public UserReadDto findById(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return userReadMapper.map(userRepository.findByIdAndCompanyId(id, companyId));
    }

    @Transactional
    public UserReadDto create(UserCreateDto userCreateDto, Long companyId){

        User user = userCreateMapper.map(userCreateDto);
        user.setCompany(companyRepository.getCompanyById(companyId));
        if (user.getUserStatus() == null) {
            user.setUserStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);

        return userReadMapper.map(user);
    }

    @Transactional
    public UserReadDto update(Long userId, UserUpdateDto dto) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        User user = userRepository.findByIdAndCompanyId(userId, companyId);
        userUpdateMapper.map(dto, user);

        return userReadMapper.map(user);
    }

    public UserReadDto findByUsername(String username){
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return userReadMapper.map(userRepository.findByUsernameAndCompanyId(username, companyId));
    }
}

