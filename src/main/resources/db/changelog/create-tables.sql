--liquibase formatted sql

--changeset MariaKolyvanova:1
CREATE TABLE IF NOT EXISTS companies
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    region      VARCHAR(128),
    industry    VARCHAR(128),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:2
CREATE TABLE IF NOT EXISTS users
(
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(64) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(64),
    last_name   VARCHAR(64),
    company_id  BIGINT REFERENCES companies (id) ON DELETE CASCADE,
    role        VARCHAR(32) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:3
CREATE TABLE IF NOT EXISTS audit_logs
(
    id          BIGSERIAL PRIMARY KEY,
    company_id  BIGINT REFERENCES companies (id) ON DELETE SET NULL,
    user_id     BIGINT,
    user_name   VARCHAR(255),
    action      VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:4
CREATE TABLE IF NOT EXISTS initial_data
(
    id          BIGSERIAL PRIMARY KEY,
    company_id  BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    type_file   VARCHAR(64) NOT NULL,
    period      VARCHAR(32) NOT NULL,
    status      VARCHAR(32) NOT NULL,
    file_path   VARCHAR(512) NOT NULL,
    file_size   BIGINT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:5
CREATE TABLE IF NOT EXISTS indicators
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255)       NOT NULL,
    code            VARCHAR(64) UNIQUE NOT NULL,
    category        VARCHAR(64)        NOT NULL,
    formula         TEXT,
    normative_value DOUBLE PRECISION,
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:6
CREATE TABLE IF NOT EXISTS raw_data
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES initial_data (id) ON DELETE CASCADE,
    indicator_id BIGINT REFERENCES indicators (id),
    row_index    INTEGER,
    cell_address VARCHAR(16),
    value        TEXT,
    is_valid     BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:7
CREATE TABLE IF NOT EXISTS indicator_values
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES initial_data (id) ON DELETE CASCADE,
    indicator_id BIGINT REFERENCES indicators (id),
    value        DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:8
CREATE TABLE IF NOT EXISTS recommendations
(
    id             BIGSERIAL PRIMARY KEY,
    indicator_id   BIGINT REFERENCES indicators (id) ON DELETE CASCADE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT NOT NULL,
    condition_rule TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:9
CREATE TABLE IF NOT EXISTS analysis_results
(
    id          BIGSERIAL PRIMARY KEY,
    report_id   BIGINT REFERENCES initial_data (id) ON DELETE CASCADE,
    user_id  BIGINT    REFERENCES users (id) ON DELETE SET NULL,
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:10
CREATE TABLE IF NOT EXISTS accepted_recommendations
(
    id              BIGSERIAL PRIMARY KEY,
    analysis_results_id     BIGINT REFERENCES analysis_results (id) ON DELETE CASCADE,
    recommendation_id BIGINT REFERENCES recommendations (id),
    description     TEXT,
    status          VARCHAR(32) DEFAULT 'ACCEPTED',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


--changeset MariaKolyvanova:11
CREATE TABLE IF NOT EXISTS report_exports
(
    id          BIGSERIAL PRIMARY KEY,
    analysis_results_id BIGINT REFERENCES analysis_results (id) ON DELETE CASCADE,
    export_format VARCHAR(16) NOT NULL,
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(512) NOT NULL,
    file_size   BIGINT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

