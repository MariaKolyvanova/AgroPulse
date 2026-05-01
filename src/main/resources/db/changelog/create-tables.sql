--liquibase formatted sql

--changeset MariaKolyvanova:1
-- Организационный слой: Компания
CREATE TABLE IF NOT EXISTS companies
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    region      VARCHAR(128),
    industry    VARCHAR(128)
);

--changeset MariaKolyvanova:2
-- Организационный слой: Пользователь
CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT REFERENCES companies (id) ON DELETE CASCADE,

    role            VARCHAR(32) NOT NULL,
    username        VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(128) NOT NULL,

    last_name       VARCHAR(100),
    first_name      VARCHAR(100),
    patronymic      VARCHAR(100),

    user_status     VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',

    created_date      TIMESTAMP,
    modified_date     TIMESTAMP,
    created_by      VARCHAR(32),
    modified_by     VARCHAR(32)
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
    unit            VARCHAR(32)
);

--changeset MariaKolyvanova:5
-- Нормативный слой: Рекомендация (Шаблоны)
CREATE TABLE IF NOT EXISTS recommendations
(
    id             BIGSERIAL PRIMARY KEY,
    indicator_id   BIGINT REFERENCES indicators (id) ON DELETE CASCADE,
    name           VARCHAR(255) NOT NULL,
    description    TEXT NOT NULL,
    condition_rule TEXT
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
    size       BIGINT
);


CREATE TABLE IF NOT EXISTS report_audit
(
    id              BIGSERIAL PRIMARY KEY,

    user_id         BIGINT REFERENCES users (id) ON DELETE CASCADE,
    action          VARCHAR(32) NOT NULL,

    report_id       BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    report_status   VARCHAR(32),

    created_date     TIMESTAMP NOT NULL DEFAULT now()
);

--changeset MariaKolyvanova:7
-- Операционный слой: Сырые данные
CREATE TABLE IF NOT EXISTS raw_data
(
    id             BIGSERIAL PRIMARY KEY,
    report_id      BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    indicator_name VARCHAR(255),
    value          TEXT,
    error_text     TEXT
);

--changeset MariaKolyvanova:8
-- Операционный слой: Значение показателя
CREATE TABLE IF NOT EXISTS indicator_values
(
    id           BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    indicator_id BIGINT REFERENCES indicators (id),
    value        DOUBLE PRECISION NOT NULL
);

--changeset MariaKolyvanova:9
-- Аналитический слой: Комментарии
CREATE TABLE IF NOT EXISTS report_comments
(
    id          BIGSERIAL PRIMARY KEY,
    report_id   BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    comment     TEXT NOT NULL
);

--changeset MariaKolyvanova:10
-- Аналитический слой: Принятая рекомендация
CREATE TABLE IF NOT EXISTS accepted_recommendations
(
    id                BIGSERIAL PRIMARY KEY,
    report_id         BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    user_id           BIGINT REFERENCES users (id) ON DELETE SET NULL,
    recommendation_id BIGINT REFERENCES recommendations (id)
);