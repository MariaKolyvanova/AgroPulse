package ru.ochakovo.agropulse.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.entity.organization.User;

@Mapper(componentModel = "spring")
public abstract class UserCreateMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    public abstract User map(UserCreateDto userCreateDto);

    @AfterMapping
    protected void encodePassword(UserCreateDto dto, @MappingTarget User user) {
        if (StringUtils.hasText(dto.password())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
    }
}
