-- Демо-данные: компания, пользователи, значения ESG.
-- Используется Liquibase при старте приложения и @Sql в интеграционных тестах.

INSERT INTO companies (id, name, region, industry)
VALUES
    (1, 'АО МПБК Очаково', 'Москва', 'Пищевая промышленность')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    region = EXCLUDED.region,
    industry = EXCLUDED.industry;

INSERT INTO users (id, company_id, role, username, password, last_name, first_name, patronymic, user_status, created_date, modified_date, created_by, modified_by)
VALUES
    (1, 1, 'ADMINISTRATOR', 'admin@ochakovo.ru', '{noop}123', 'Иванов', 'Иван', 'Иванович', 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (2, 1, 'OPERATOR', 'operator_1@ochakovo.ru', '{noop}123', 'Петров', 'Петр', 'Сидорович', 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (3, 1, 'ANALYTIC', 'analyst_expert@ochakovo.ru', '{noop}123', 'Сидорова', 'Анна', 'Павловна', 'ACTIVE', NOW(), NOW(), NULL, NULL)
ON CONFLICT (id) DO UPDATE SET
    company_id = EXCLUDED.company_id,
    role = EXCLUDED.role,
    username = EXCLUDED.username,
    password = EXCLUDED.password,
    last_name = EXCLUDED.last_name,
    first_name = EXCLUDED.first_name,
    patronymic = EXCLUDED.patronymic,
    user_status = EXCLUDED.user_status,
    modified_date = EXCLUDED.modified_date;

INSERT INTO values_indicators_esg (company_id, report_year, indicator_id, value)
VALUES
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'LABOR_PRODUCTIVITY'), 790.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'OCCUPATIONAL_SAFETY'), 90.2),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'PAYROLL_SOCIAL_PROGRAMS'), 16200.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TURNOVER'), 10.5),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TRAINING'), 61.5),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'AIR_EMISSIONS'), 1310.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'GHG_EMISSIONS'), 43500.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'ENERGY_EFFICIENCY'), 298000.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'WATER_USE'), 293000.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'WASTE_MANAGEMENT'), 14800.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'SOCIAL_INVESTMENTS'), 3800.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'RESPONSIBLE_FINANCING'), 10.20),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'CYBERSECURITY'), 6.5),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'LEADERSHIP_ENGAGEMENT'), 6.0),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'RISK_MANAGEMENT'), 6.2),
    (1, 2023, (SELECT id FROM indicators_esg WHERE code = 'SUSTAINABILITY_FOCUS'), 65.0)
ON CONFLICT (company_id, report_year, indicator_id) DO UPDATE SET value = EXCLUDED.value;

INSERT INTO values_indicators_esg (company_id, report_year, indicator_id, value)
VALUES
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'LABOR_PRODUCTIVITY'), 850.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'OCCUPATIONAL_SAFETY'), 92.5),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'PAYROLL_SOCIAL_PROGRAMS'), 18500.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TURNOVER'), 11.4),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TRAINING'), 68.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'AIR_EMISSIONS'), 1250.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'GHG_EMISSIONS'), 42150.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'ENERGY_EFFICIENCY'), 310000.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'WATER_USE'), 285000.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'WASTE_MANAGEMENT'), 14300.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'SOCIAL_INVESTMENTS'), 4200.0),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'RESPONSIBLE_FINANCING'), 12.50),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'CYBERSECURITY'), 7.2),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'LEADERSHIP_ENGAGEMENT'), 6.5),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'RISK_MANAGEMENT'), 6.8),
    (1, 2024, (SELECT id FROM indicators_esg WHERE code = 'SUSTAINABILITY_FOCUS'), 70.0)
ON CONFLICT (company_id, report_year, indicator_id) DO UPDATE SET value = EXCLUDED.value;

INSERT INTO values_indicators_esg (company_id, report_year, indicator_id, value)
VALUES
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'LABOR_PRODUCTIVITY'), 920.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'OCCUPATIONAL_SAFETY'), 94.1),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'PAYROLL_SOCIAL_PROGRAMS'), 21200.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TURNOVER'), 13.2),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'STAFF_TRAINING'), 72.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'AIR_EMISSIONS'), 1180.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'GHG_EMISSIONS'), 40800.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'ENERGY_EFFICIENCY'), 325000.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'WATER_USE'), 278000.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'WASTE_MANAGEMENT'), 13950.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'SOCIAL_INVESTMENTS'), 5100.0),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'RESPONSIBLE_FINANCING'), 15.80),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'CYBERSECURITY'), 7.8),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'LEADERSHIP_ENGAGEMENT'), 7.1),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'RISK_MANAGEMENT'), 7.4),
    (1, 2025, (SELECT id FROM indicators_esg WHERE code = 'SUSTAINABILITY_FOCUS'), 78.0)
ON CONFLICT (company_id, report_year, indicator_id) DO UPDATE SET value = EXCLUDED.value;

SELECT setval('companies_id_seq', (SELECT MAX(id) FROM companies));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('indicators_financial_reports_id_seq', (SELECT MAX(id) FROM indicators_financial_reports));
SELECT setval('indicators_financial_analyze_id_seq', (SELECT MAX(id) FROM indicators_financial_analyze));
SELECT setval('recommendations_id_seq', (SELECT MAX(id) FROM recommendations));
SELECT setval('reports_id_seq', (SELECT MAX(id) FROM reports));
SELECT setval('values_indicators_financial_reports_id_seq', (SELECT MAX(id) FROM values_indicators_financial_reports));
SELECT setval('indicators_esg_id_seq', COALESCE((SELECT MAX(id) FROM indicators_esg), 1));
SELECT setval('values_indicators_esg_id_seq', COALESCE((SELECT MAX(id) FROM values_indicators_esg), 1));
SELECT setval('report_comments_id_seq', (SELECT MAX(id) FROM report_comments));
