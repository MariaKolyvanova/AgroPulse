--liquibase formatted sql

--changeset MariaKolyvanova:1
CREATE TABLE IF NOT EXISTS companies
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    region      VARCHAR(128),
    industry    VARCHAR(128)
);

--changeset MariaKolyvanova:2
CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,

    role            VARCHAR(32) NOT NULL,
    username        VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(128) NOT NULL,

    last_name       VARCHAR(100),
    first_name      VARCHAR(100),
    patronymic      VARCHAR(100),

    user_status     VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',

    created_date    TIMESTAMP,
    modified_date   TIMESTAMP,
    created_by      VARCHAR(32),
    modified_by     VARCHAR(32)
);

--changeset MariaKolyvanova:4
CREATE TABLE IF NOT EXISTS indicators_financial_reports
(
    id              BIGSERIAL PRIMARY KEY,
    parent_tag      VARCHAR(64),
    code            VARCHAR(64) NOT NULL UNIQUE,
    xml_tag         VARCHAR(255),
    indicator_name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS indicators_financial_analyze
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    code         VARCHAR(64)  NOT NULL UNIQUE,
    formula      TEXT,
    normal_min   NUMERIC(5, 2),
    normal_max   NUMERIC(5, 2),
    category     VARCHAR(100),
    unit         VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS recommendations
(
    id                  BIGSERIAL PRIMARY KEY,
    indicator_id        BIGINT NOT NULL,

    condition_rule      TEXT NOT NULL,
    conclusion_text     TEXT,
    recommendation_text TEXT,
    condition           VARCHAR(20) NOT NULL,

    CONSTRAINT uq_recommendations_indicator_condition UNIQUE (indicator_id, condition_rule),

    CONSTRAINT recommendations_indicator_id_fkey
        FOREIGN KEY (indicator_id)
            REFERENCES indicators_financial_analyze (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_recommendations_condition
        CHECK (condition IN ('normal', 'warning', 'critical'))
);

--changeset MariaKolyvanova:6
CREATE TABLE IF NOT EXISTS reports
(
    id BIGSERIAL PRIMARY KEY,

    company_id BIGINT NOT NULL
        REFERENCES companies (id) ON DELETE CASCADE,

    report_year  SMALLINT NOT NULL,
    report_month SMALLINT,

    report_name VARCHAR(255) NOT NULL,

    status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED',

    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL UNIQUE,

    content_type VARCHAR(127),
    file_size    BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS report_audit
(
    id         BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(32) NOT NULL,
    report_id  BIGINT REFERENCES reports (id) ON DELETE CASCADE,
    date       TIMESTAMP NOT NULL DEFAULT NOW()
);

--changeset MariaKolyvanova:8
CREATE TABLE IF NOT EXISTS values_indicators_financial_reports
(
    id                    BIGSERIAL PRIMARY KEY,
    report_id             BIGINT NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    indicator_id          BIGINT NOT NULL REFERENCES indicators_financial_reports (id),
    value                 DOUBLE PRECISION NOT NULL,
    previous_period_value DOUBLE PRECISION,
    two_periods_ago_value DOUBLE PRECISION,

    CONSTRAINT uq_vifr_report_indicator UNIQUE (report_id, indicator_id)
);

CREATE TABLE IF NOT EXISTS values_indicators_financial_analyze
(
    id                     BIGSERIAL PRIMARY KEY,
    report_id              BIGINT NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    financial_indicator_id BIGINT NOT NULL REFERENCES indicators_financial_analyze (id),
    value                  DOUBLE PRECISION NOT NULL,
    previous_period_value  DOUBLE PRECISION,
    two_periods_ago_value  DOUBLE PRECISION,

    CONSTRAINT uq_vifa_report_indicator UNIQUE (report_id, financial_indicator_id)
);

--changeset MariaKolyvanova:9
CREATE TABLE IF NOT EXISTS report_comments
(
    id        BIGSERIAL PRIMARY KEY,
    report_id BIGINT NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    comment   TEXT NOT NULL
);

--changeset MariaKolyvanova:10
CREATE TABLE IF NOT EXISTS auth_audit
(
    id         BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies (id) ON DELETE CASCADE,
    user_id    BIGINT REFERENCES users (id) ON DELETE SET NULL,
    auth_at    TIMESTAMP NOT NULL,
    ip         VARCHAR(50)
);

--changeset MariaKolyvanova:11
CREATE TABLE IF NOT EXISTS indicators_esg
(
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(64) NOT NULL UNIQUE,
    indicator_name VARCHAR(255) NOT NULL,
    category       VARCHAR(100) NOT NULL
);

--changeset MariaKolyvanova:12
CREATE TABLE IF NOT EXISTS values_indicators_esg
(
    id           BIGSERIAL PRIMARY KEY,
    company_id   BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    report_year  SMALLINT NOT NULL,
    indicator_id BIGINT NOT NULL REFERENCES indicators_esg (id),
    value        DOUBLE PRECISION NOT NULL,

    CONSTRAINT uq_vie_company_year_indicator UNIQUE (company_id, report_year, indicator_id)
);

--changeset MariaKolyvanova:13
CREATE TABLE IF NOT EXISTS recommendations_esg
(
    id                  BIGSERIAL PRIMARY KEY,
    indicator_id        BIGINT NOT NULL,

    condition_rule      TEXT NOT NULL,
    conclusion_text     TEXT,
    recommendation_text TEXT,
    is_positive         BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_recommendations_esg_indicator_condition UNIQUE (indicator_id, condition_rule),

    CONSTRAINT recommendations_esg_indicator_id_fkey
        FOREIGN KEY (indicator_id)
            REFERENCES indicators_esg (id)
            ON DELETE CASCADE
);
