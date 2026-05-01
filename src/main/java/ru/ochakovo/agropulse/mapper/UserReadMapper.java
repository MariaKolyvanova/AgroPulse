package ru.ochakovo.agropulse.mapper;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.UserUpdateDto;
import ru.ochakovo.agropulse.entity.organization.User;

@Mapper(componentModel = "spring")
public interface UserReadUpdateMapper {
    UserUpdateDto map(User user);
}
