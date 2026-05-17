-- ПАРОЛЬ - '{noop}123'

INSERT INTO companies (id, name, region, industry)
VALUES
    (1, 'ЗАО Очаково', 'Москва', 'Пищевая промышленность'),
    (2, 'АгроТех', 'Краснодарский край', 'Сельское хозяйство'),
    (3, 'ФинТех Солюшнс', 'Новосибирск', 'Информационные технологии');

INSERT INTO users (id, company_id, role, username, password, last_name, first_name, patronymic, user_status, created_date, modified_date, created_by, modified_by)
VALUES
    (1, 1, 'ADMINISTRATOR', 'admin@ochakovo.ru', '{noop}123', 'Иванов', 'Иван', 'Иванович', 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (2, 1, 'OPERATOR', 'operator_1@ochakovo.ru', '{noop}123', 'Петров', 'Петр', 'Сидорович', 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (3, 1, 'ANALYTIC', 'analyst_expert@ochakovo.ru', '{noop}123', 'Сидорова', 'Анна', 'Павловна', 'ACTIVE', NOW(), NOW(), NULL, NULL);

-- Сброс последовательностей (автоинкрементов)
SELECT setval('companies_id_seq', (SELECT MAX(id) FROM companies));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('indicators_financial_reports_id_seq', (SELECT MAX(id) FROM indicators_financial_reports));
SELECT setval('indicators_financial_analyze_id_seq', (SELECT MAX(id) FROM indicators_financial_analyze));
SELECT setval('recommendations_id_seq', (SELECT MAX(id) FROM recommendations));
SELECT setval('reports_id_seq', (SELECT MAX(id) FROM reports));
SELECT setval('raw_data_id_seq', (SELECT MAX(id) FROM raw_data));
SELECT setval('values_indicators_financial_reports_id_seq', (SELECT MAX(id) FROM values_indicators_financial_reports));
SELECT setval('report_comments_id_seq', (SELECT MAX(id) FROM report_comments));
SELECT setval('accepted_recommendations_id_seq', (SELECT MAX(id) FROM accepted_recommendations));