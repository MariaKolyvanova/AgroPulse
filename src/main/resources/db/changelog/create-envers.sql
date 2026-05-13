--liquibase formatted sql

--changeset maria:1
CREATE TABLE IF NOT EXISTS revinfo
(
    rev SERIAL PRIMARY KEY,
    revtstmp BIGINT
);

--changeset maria:1.1
ALTER TABLE revinfo
    ADD COLUMN IF NOT EXISTS username VARCHAR(255);

--changeset maria:2
CREATE TABLE IF NOT EXISTS users_aud
(
    id          BIGINT  NOT NULL,
    rev         INTEGER NOT NULL,
    revtype     SMALLINT,

    role        VARCHAR(32),
    username    VARCHAR(255),
    last_name   VARCHAR(100),
    first_name  VARCHAR(100),
    patronymic  VARCHAR(100),
    user_status VARCHAR(32),

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_users_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

