--liquibase formatted sql

--changeset MariaKolyvanova:1
-- Организационный слой: Компания
CREATE TABLE IF NOT EXISTS companies
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    region      VARCHAR(128),
    industry    VARCHAR(128),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:2
-- Организационный слой: Пользователь
CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL PRIMARY KEY,
    companies_id BIGINT REFERENCES companies (id) ON DELETE CASCADE,
    role            VARCHAR(32) NOT NULL, -- Enum: ADMINISTRATOR, OPERATOR, ANALYTIC
    email           VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255),
    user_status     VARCHAR(32) NOT NULL DEFAULT 'ACTIVE', -- Enum: ACTIVE, ARCHIVE
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:3
-- Организационный слой: Лог аудита
CREATE TABLE IF NOT EXISTS audit_logs
(
    id              BIGSERIAL PRIMARY KEY,
    companies_id BIGINT REFERENCES companies (id) ON DELETE SET NULL,
    user_id         BIGINT,
    entity_type     VARCHAR(64),
    entity_name     TEXT,
    action          VARCHAR(255) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:4
-- Нормативный слой: Показатель
CREATE TABLE IF NOT EXISTS indicators
(
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64) UNIQUE NOT NULL,
    name            VARCHAR(255) NOT NULL,
    category        VARCHAR(128),
    value_type      VARCHAR(32) NOT NULL, -- Enum: IMPORTED, CALCULATED
    formula         TEXT,
    normative_value TEXT,
    unit            VARCHAR(32),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:5
-- Нормативный слой: Рекомендация (Шаблоны)
CREATE TABLE IF NOT EXISTS recommendations
(
    id             BIGSERIAL PRIMARY KEY,
    indicator_id   BIGINT REFERENCES indicators (id) ON DELETE CASCADE,
    name           VARCHAR(255) NOT NULL,
    description    TEXT NOT NULL,
    condition_rule TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:6
-- Операционный слой: Отчёт (включает атрибуты класса File)
CREATE TABLE IF NOT EXISTS reports
(
    id              BIGSERIAL PRIMARY KEY,
    companies_id BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    period          VARCHAR(32) NOT NULL,
    document_type   VARCHAR(64) NOT NULL,
    status          VARCHAR(32) NOT NULL,

    name       VARCHAR(255),
    path       VARCHAR(512),
    format     VARCHAR(16),
    size       BIGINT,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:7
-- Операционный слой: Сырые данные
CREATE TABLE IF NOT EXISTS raw_data
(
    id             BIGSERIAL PRIMARY KEY,
    report_id      BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    indicator_name VARCHAR(255),
    value          TEXT,
    error_text     TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:8
-- Операционный слой: Значение показателя
CREATE TABLE IF NOT EXISTS indicator_values
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    indicator_id BIGINT REFERENCES indicators (id),
    value        DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:9
-- Аналитический слой: Комментарии
CREATE TABLE IF NOT EXISTS report_comments
(
    id          BIGSERIAL PRIMARY KEY,
    report_id   BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    comment     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset MariaKolyvanova:10
-- Аналитический слой: Принятая рекомендация
CREATE TABLE IF NOT EXISTS accepted_recommendations
(
    id                BIGSERIAL PRIMARY KEY,
    report_id         BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    user_id           BIGINT REFERENCES users (id) ON DELETE SET NULL,
    recommendation_id BIGINT REFERENCES recommendations (id),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);