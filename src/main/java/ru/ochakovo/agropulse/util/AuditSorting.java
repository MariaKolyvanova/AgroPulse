package ru.ochakovo.agropulse.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class AuditSorting {

    private AuditSorting() {
    }

    public static Pageable byDateDesc(Pageable pageable, String dateProperty) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, dateProperty)
        );
    }
}
