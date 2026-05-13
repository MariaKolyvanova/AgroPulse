package ru.ochakovo.agropulse.service;

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
import ru.ochakovo.agropulse.repository.CompanyRepository;
import ru.ochakovo.agropulse.repository.UserRepository;

import java.util.List;
import java.util.Optional;


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

//    public Page<UserReadDto> findAll(UserFilter filter, Pageable pageable){
//        var predicate = QPredicates.builder()
//                .add(filter.lastName(), user.lastName::containsIgnoreCase)
//                .add(filter.firstName(), user.firstName::containsIgnoreCase)
//                .add(filter.patronymic(), user.patronymic::containsIgnoreCase)
//                .add(filter.role(), user.role::eq)
//                .add(filter.userStatus(), user.userStatus::eq)
//                .build();
//
//        return userRepository.findAll(predicate, pageable)
//                .map(userReadMapper::map);
//    }

    public UserReadDto findById(Long id) {
        return userRepository.findById(id)
                .map(userReadMapper::map).orElseThrow();
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
    public Optional<UserReadDto> update(Long userId, UserUpdateDto dto) {

        return userRepository.findById(userId)
                .map(user -> {
                    userUpdateMapper.map(dto, user);
                    return userReadMapper.map(user);
                });
    }


    public Optional<UserReadDto> findByUsername(String username){
        return userRepository.findByUsername(username).map(userReadMapper::map);
    }

}

