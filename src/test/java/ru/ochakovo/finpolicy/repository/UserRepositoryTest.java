package ru.ochakovo.finpolicy.database.repository;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.finpolicy.IntegrationTestBase;
import ru.ochakovo.finpolicy.annotation.IT;

class UserRepositoryTest extends IntegrationTestBase {

    @Autowired
    UserRepository userRepository;

    @Test
    void checkFindAllBy(){
        var users = userRepository.findAllBy("а", "ов");
    }

    @Test
    void checkFindAllByCompanyId(){
        var users = userRepository.findAllByCompanyId(1L);
    }
}