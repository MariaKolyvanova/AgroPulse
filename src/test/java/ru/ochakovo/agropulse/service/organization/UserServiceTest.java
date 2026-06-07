package ru.ochakovo.agropulse.service.organization;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.IT;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.organization.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IT
@WithMockUser(username = "admin@ochakovo.ru", roles = "ADMINISTRATOR")
@Transactional
@RequiredArgsConstructor
class UserServiceTest extends IntegrationTestBase {

    @Autowired
    UserService userService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompanyService companyService;

    private final static Long USER_1 = 1L;

    @Test
    void findAll(){
        List<UserReadDto> result = userService.findAll(1L);
        assertThat(result).hasSize(3);
    }

    @Test
    void findById(){
    }

    @Test
    void create(){
        var userDto = new UserCreateDto(
                Role.OPERATOR,
                "abc@bk.ru",
                UserStatus.ARCHIVE,
                "pass123",
                "Петров",
                "Иван",
                "Альбертович"
        );

        UserReadDto actualResult = userService.create(userDto, 1L);
        assertEquals(userDto.username(), actualResult.username());
        assertEquals(userDto.lastName(), actualResult.lastName());
        assertEquals(userDto.firstName(), actualResult.firstName());
        assertEquals(userDto.patronymic(), actualResult.patronymic());
        assertEquals(userDto.role(), actualResult.role());
    }

}