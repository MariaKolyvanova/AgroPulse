package ru.ochakovo.agropulse.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.ochakovo.agropulse.dto.user.UserUpdateDto;
import ru.ochakovo.agropulse.entity.organization.User;

@Mapper(componentModel = "spring")
public abstract class UserUpdateMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", ignore = true)
    public abstract void map(UserUpdateDto userUpdateDto, @MappingTarget User user);

    @AfterMapping
    protected void encodePassword(UserUpdateDto dto, @MappingTarget User user) {
        if (StringUtils.hasText(dto.password())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
    }
}
