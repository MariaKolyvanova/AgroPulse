package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.dto.company.InfoCompanyDto;
import ru.ochakovo.agropulse.entity.organization.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, QuerydslPredicateExecutor<Company> {

    boolean existsByName(String nameCompany);

    Company getCompanyById(Long idCompany);

    Company getCompanyByName(String nameCompany);

    boolean existsById(Long idCompany);

    @Query(value = """
                SELECT
                	(SELECT count(*) FROM reports r
                	    WHERE r.company_id = :companyId) AS reportsSize,
            
                	(SELECT count(*) FROM users u
                	    WHERE u.company_id = :companyId) AS usersSize,
            
                	(SELECT count(*) FROM users u
                	    WHERE u.company_id = :companyId AND u.user_status = 'ACTIVE') AS usersActiveSize
            """, nativeQuery = true)
    InfoCompanyDto getInfoByCompany(@Param("companyId") Long companyId);
}
