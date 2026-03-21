package ru.ochakovo.finpolicy.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import ru.ochakovo.finpolicy.database.entity.organization.Company;
import ru.ochakovo.finpolicy.database.entity.organization.User;

import java.util.List;

@Repository
public interface UserRepository
        extends
        JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User>
{
    @Query("select u from User u " +
            "where u.firstname like %:firstname% and u.lastname like %:lastname%")
    List<User> findAllBy(String firstname, String lastname);

    List<User> findAllByCompanyId(Long companyId);

    boolean existsByUsername(String username);

}
