package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ochakovo.agropulse.entity.dictionary.Indicator;
import ru.ochakovo.agropulse.entity.dictionary.ValueSource;

import java.util.List;
import java.util.Optional;

public interface IndicatorRepository extends JpaRepository<Indicator, Long> {

    Optional<Indicator> findByCode(String code);

    List<Indicator> findByXmlTag(String xmlTag);

    List<Indicator> findByValueSource(ValueSource valueSource);
}
