package ru.ochakovo.agropulse.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.entity.organization.User;

@Mapper(componentModel = "spring")
public interface UserReadMapper {
    @Mapping(source = "company.id", target = "companyId")
    UserReadDto map(User user);
}
