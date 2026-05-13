package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.data.Report;

import java.util.Optional;

@Repository
public interface ReportRepository  extends JpaRepository<Report, Long>, QuerydslPredicateExecutor<Report> {

    Optional<Report> findByIdAndCompany_Id(Long id, Long companyId);

}
