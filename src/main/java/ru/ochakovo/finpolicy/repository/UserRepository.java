package ru.ochakovo.finpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.finpolicy.entity.organization.User;

@Repository
public interface UserRepository
        extends
        JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User>
{
    @Query("select u from User u " +
            "where u.fullName like %:name%")

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findUserById(Long userId);
}
