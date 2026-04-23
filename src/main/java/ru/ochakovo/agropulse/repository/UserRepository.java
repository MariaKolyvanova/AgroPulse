package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.organization.User;

@Repository
public interface UserRepository
        extends
        JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User>
{


    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findUserById(Long userId);
}
