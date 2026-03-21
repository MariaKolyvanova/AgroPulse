--liquibase formatted sql

--changeset MariaKolyvanova:1
CREATE TABLE IF NOT EXISTS companies
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:2
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(64) UNIQUE NOT NULL,
    password   VARCHAR(255)       NOT NULL,
    firstname  VARCHAR(64),
    lastname   VARCHAR(64),
    company_id BIGINT             REFERENCES companies (id) ON DELETE SET NULL,
    role       VARCHAR(32)        NOT NULL,
    created_at TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:3
CREATE TABLE IF NOT EXISTS audit_logs
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       REFERENCES users (id) ON DELETE SET NULL,
    action      VARCHAR(255) NOT NULL,
    entity_name VARCHAR(100),
    entity_id   BIGINT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

--changeset MariaKolyvanova:4
CREATE TABLE IF NOT EXISTS financial_reports
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    type_file   VARCHAR(64)  NOT NULL,
    period_year INTEGER      NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    company_id  BIGINT       NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:5
CREATE TABLE IF NOT EXISTS indicators
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255)       NOT NULL,
    code            VARCHAR(64) UNIQUE NOT NULL,
    category        VARCHAR(32)        NOT NULL,
    formula         TEXT,
    normative_value DOUBLE PRECISION,
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:6
CREATE TABLE IF NOT EXISTS raw_data
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES financial_reports (id) ON DELETE CASCADE,
    row_index    INTEGER,
    cell_address VARCHAR(16),
    raw_value    TEXT,
    indicator_id BIGINT REFERENCES indicators (id),
    is_valid     BOOLEAN            DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:7
CREATE TABLE IF NOT EXISTS indicator_values
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES financial_reports (id) ON DELETE CASCADE,
    indicator_id BIGINT REFERENCES indicators (id),
    value        DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:8
CREATE TABLE IF NOT EXISTS recommendations
(
    id             BIGSERIAL PRIMARY KEY,
    indicator_id   BIGINT REFERENCES indicators (id),
    title          VARCHAR(255) NOT NULL,
    text_content   TEXT         NOT NULL,
    priority       VARCHAR(16)           DEFAULT 'LOW',
    condition_rule TEXT,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:9
CREATE TABLE IF NOT EXISTS analysis_results
(
    id          BIGSERIAL PRIMARY KEY,
    report_id   BIGINT REFERENCES financial_reports (id) ON DELETE CASCADE,
    analyst_id  BIGINT    REFERENCES users (id) ON DELETE SET NULL,
    total_score DOUBLE PRECISION,
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:10
CREATE TABLE IF NOT EXISTS report_exports
(
    id            BIGSERIAL PRIMARY KEY,
    analysis_id   BIGINT REFERENCES analysis_results (id) ON DELETE CASCADE,
    user_id       BIGINT       REFERENCES users (id) ON DELETE SET NULL,
    file_path     VARCHAR(512) NOT NULL,
    export_format VARCHAR(16)  NOT NULL,
    file_size     BIGINT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

