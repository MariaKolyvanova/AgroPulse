package ru.ochakovo.agropulse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.dto.audit.UserAuditRow;
import ru.ochakovo.agropulse.entity.organization.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsernameAndCompanyId(String username, Long companyId);

    List<User> findAllByCompanyId(Long companyId);

    @EntityGraph(attributePaths = "company")
    Optional<User> findByUsername(String username);

    @Query(value = """
                      SELECT
                      us.id,
                      (us.last_name || ' ' || us.first_name || ' ' || us.patronymic) AS modifiedByFullName,
                      ua.id,
                      (ua.last_name || ' ' || ua.first_name || ' ' || ua.patronymic) AS modifiedUserFullName,
                      to_timestamp(r.revtstmp / 1000) AS modifiedAt
                      FROM revinfo r
                      JOIN users_aud ua ON r.rev = ua.rev
                      JOIN users us ON us.username = r.username
            """, countQuery = """
                SELECT count(*)
            FROM revinfo r
            JOIN users_aud ua ON r.rev = ua.rev
            JOIN users us ON us.username = r.username
            
            """, nativeQuery = true)
    Page<UserAuditRow> findUserAudits(@Param("modifiedByFullName") String modifiedByFullName,
                                      @Param("modifiedUserFullName") String modifiedUserFullName,
                                      @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
                                      Pageable pageable);

}
