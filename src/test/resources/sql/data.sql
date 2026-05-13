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

INSERT INTO indicators (id, code, parent_tag, xml_tag, indicator_name, value_source, formula, normative_value, unit)
VALUES
    (1, 'Vyr', NULL, 'Vyr', 'Выручка', 'IMPORTED', NULL, '> 0', 'руб.'),
    (2, 'Seb', NULL, 'Seb', 'Себестоимость', 'IMPORTED', NULL, NULL, 'руб.'),
    (3, 'ValPr', NULL, '-', 'Валовая прибыль', 'CALCULATED', '[Vyr] - [Seb]', '> 100000', 'руб.'),
    (4, 'K_Liquidity', NULL, '-', 'Коэффициент текущей ликвидности', 'CALCULATED', '[ObA] / [KratObyaz]', '1.5 - 2.5', 'ед.');

INSERT INTO recommendations (id, indicator_id, name, description, condition_rule)
VALUES
    (1, 3, 'Снижение маржинальности', 'Рекомендуется провести аудит закупочных цен для снижения себестоимости.', 'ValPr < 100000'),
    (2, 4, 'Критическая ликвидность', 'Риск невыполнения краткосрочных обязательств. Требуется оптимизация дебиторской задолженности.', 'K_Liquidity < 1.5');

-- Вставка данных в таблицу reports (обновлённая структура)
INSERT INTO reports (id, company_id, report_year, report_month, report_name, status, original_filename, stored_filename, content_type, file_size, created_at, updated_at)
VALUES
    (1, 1, 2025, 10, 'Финансовый отчёт за октябрь 2025', 'UPLOADED', 'fin_report_2025_10.xml', 'report_1_2025_10.xml', 'application/xml', 15420, NOW(), NOW()),
    (2, 1, 2025, 9, 'Финансовый отчёт за сентябрь 2025', 'PROCESSING', 'fin_report_2025_09.xml', 'report_2_2025_09.xml', 'application/xml', 14800, NOW(), NOW()),
    (3, 2, 2025, 10, 'Агроотчёт октябрь 2025',  'UPLOADED', 'agro_report_2025_10.xlsx', 'report_3_2025_10.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 24500, NOW(), NOW()),
    (4, 3, 2025, 10, 'Финансовый анализ', 'FAILED', 'fin_analytics.pdf', 'report_4_2025_10.pdf', 'application/pdf', 5600, NOW(), NOW());
INSERT INTO raw_data (id, report_id, indicator_name, value, error_text)
VALUES
    (1, 1, 'Выручка от реализации', '1500000', NULL),
    (2, 1, 'Себестоимость продаж', '1450000', NULL),
    (3, 1, 'Некорректный тег', 'NaN', 'Ошибка формата числа в XML');

INSERT INTO indicator_values (id, report_id, indicator_id, value)
VALUES
    (1, 1, 1, 1500000),
    (2, 1, 2, 1450000),
    (3, 1, 3, 50000),
    (4, 2, 1, 1200000);

INSERT INTO report_comments (id, report_id, comment)
VALUES
    (1, 1, 'Зафиксировано аномальное повышение себестоимости в текущем квартале.'),
    (2, 1, 'Данные проверены, расхождения с первичной документацией отсутствуют.');

INSERT INTO accepted_recommendations (id, report_id, user_id, recommendation_id)
VALUES
    (1, 1, 3, 1),
    (2, 2, 3, 2);

-- Сброс последовательностей (автоинкрементов)
SELECT setval('companies_id_seq', (SELECT MAX(id) FROM companies));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('indicators_id_seq', (SELECT MAX(id) FROM indicators));
SELECT setval('recommendations_id_seq', (SELECT MAX(id) FROM recommendations));
SELECT setval('reports_id_seq', (SELECT MAX(id) FROM reports));
SELECT setval('raw_data_id_seq', (SELECT MAX(id) FROM raw_data));
SELECT setval('indicator_values_id_seq', (SELECT MAX(id) FROM indicator_values));
SELECT setval('report_comments_id_seq', (SELECT MAX(id) FROM report_comments));
SELECT setval('accepted_recommendations_id_seq', (SELECT MAX(id) FROM accepted_recommendations));