package ru.ochakovo.finpolicy.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.finpolicy.database.entity.organization.Company;

@Repository
public interface CompanyRepository
        extends JpaRepository<Company, Long>,
        QuerydslPredicateExecutor<Company> {

    boolean existsByName(String nameCompany);

    Company getCompanyById(Long idCompany);

    Company getCompanyByName(String nameCompany);

    boolean existsById(Long idCompany);
}
