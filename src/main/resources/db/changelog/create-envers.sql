--liquibase formatted sql

--changeset maria:1
CREATE TABLE IF NOT EXISTS revinfo
(
    rev SERIAL PRIMARY KEY,
    revtstmp BIGINT
);
