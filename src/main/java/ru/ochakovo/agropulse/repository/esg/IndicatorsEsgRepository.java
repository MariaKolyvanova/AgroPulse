package ru.ochakovo.agropulse.repository.esg;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;

import java.util.List;
import java.util.Optional;

public interface IndicatorsEsgRepository extends JpaRepository<IndicatorsEsg, Long> {

    Optional<IndicatorsEsg> findByCode(String code);

    List<IndicatorsEsg> findAllByOrderByIndicatorNameAsc();
}
