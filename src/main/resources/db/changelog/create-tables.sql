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
    parent_tag      VARCHAR(64),
    code            VARCHAR(64) UNIQUE NOT NULL,
    xml_tag         VARCHAR(255),
    indicator_name  VARCHAR(255) NOT NULL,
    value_source    VARCHAR(32) NOT NULL,
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
CREATE TABLE IF NOT EXISTS reports (
     id BIGSERIAL PRIMARY KEY,

     company_id BIGINT NOT NULL
         REFERENCES companies(id) ON DELETE CASCADE,

     report_year SMALLINT NOT NULL,
     report_month SMALLINT NOT NULL,

     report_name VARCHAR(255) NOT NULL,

     status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED',

     original_filename VARCHAR(255) NOT NULL,

     stored_filename VARCHAR(255) NOT NULL UNIQUE,

     content_type VARCHAR(127),

     file_size BIGINT NOT NULL,

     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS report_audit
(
    id              BIGSERIAL PRIMARY KEY,

    company_id      BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status          VARCHAR(32) NOT NULL,

    report_id       BIGINT REFERENCES reports (id) ON DELETE CASCADE,

    date     TIMESTAMP NOT NULL DEFAULT now()
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
    value        DOUBLE PRECISION NOT NULL,
    previous_period_value DOUBLE PRECISION,
    two_periods_ago_value DOUBLE PRECISION
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

CREATE TABLE IF NOT EXISTS auth_audit (
    id                BIGSERIAL PRIMARY KEY,
    company_id        BIGINT REFERENCES companies (id) ON DELETE CASCADE,
    user_id           BIGINT REFERENCES users (id) ON DELETE SET NULL,
    auth_at           TIMESTAMP NOT NULL,
    ip                VARCHAR(50)
);