INSERT INTO companies (name, region, industry)
VALUES
    ('ЗАО Очаково', 'Москва', 'Пищевая промышленность'),
    ('АгроТех', 'Краснодарский край', 'Сельское хозяйство'),
    ('ФинТех Солюшнс', 'Новосибирск', 'Информационные технологии');

INSERT INTO users (companies_id, role, email, password, full_name, user_status)
VALUES
    (1, 'ADMINISTRATOR', 'admin@ochakovo.ru', 'admin_pass', 'Иванов Иван Иванович', 'ACTIVE'),
    (1, 'OPERATOR', 'operator_1@ochakovo.ru', 'op_pass', 'Петров Петр Сидорович', 'ACTIVE'),
    (1, 'ANALYTIC', 'analyst_expert@ochakovo.ru', 'an_pass', 'Сидорова Анна Павловна', 'ACTIVE');

INSERT INTO indicators (code, name, category, value_type, formula, normative_value, unit)
VALUES
    ('Vyr', 'Выручка', 'Доходы', 'IMPORTED', NULL, '> 0', 'руб.'),
    ('Seb', 'Себестоимость', 'Расходы', 'IMPORTED', NULL, NULL, 'руб.'),
    ('ValPr', 'Валовая прибыль', 'Эффективность', 'CALCULATED', '[Vyr] - [Seb]', '> 100000', 'руб.'),
    ('K_Liquidity', 'Коэффициент текущей ликвидности', 'Ликвидность', 'CALCULATED', '[ObA] / [KratObyaz]', '1.5 - 2.5', 'ед.');

INSERT INTO recommendations (indicator_id, name, description, condition_rule)
VALUES
    (3, 'Снижение маржинальности', 'Рекомендуется провести аудит закупочных цен для снижения себестоимости.', 'LESS'),
    (4, 'Критическая ликвидность', 'Риск невыполнения краткосрочных обязательств. Требуется оптимизация дебиторской задолженности.', 'LESS');

INSERT INTO reports (companies_id, period, document_type, status, name, path, format, size)
VALUES
    (1, '2025-Q4', 'Бухгалтерская отчетность', 'COMPLETED', 'fin_report_2025_q4.xml', '/storage/reports/1/2025/q4.xml', 'xml', 15420),
    (1, '2025-Q3', 'Бухгалтерская отчетность', 'APPROVED', 'fin_report_2025_q3.xml', '/storage/reports/1/2025/q3.xml', 'xml', 14800);
INSERT INTO raw_data (report_id, indicator_name, value, error_text)
VALUES
    (1, 'Выручка от реализации', '1500000', NULL),
    (1, 'Себестоимость продаж', '1450000', NULL),
    (1, 'Некорректный тег', 'NaN', 'Ошибка формата числа в XML');

INSERT INTO indicator_values (report_id, indicator_id, value)
VALUES
    (1, 1, 1500000), -- Выручка
    (1, 2, 1450000), -- Себестоимость
    (1, 3, 50000),   -- Валовая прибыль (рассчитанная)
    (2, 1, 1200000); -- Выручка за прошлый период


INSERT INTO report_comments (report_id, comment)
VALUES
    (1, 'Зафиксировано аномальное повышение себестоимости в текущем квартале.'),
    (1, 'Данные проверены, расхождения с первичной документацией отсутствуют.');

INSERT INTO accepted_recommendations (report_id, user_id, recommendation_id)
VALUES
    (1, 3, 1), -- Аналитик принял рекомендацию по маржинальности для отчета №1
    (2, 3, 2); -- Аналитик принял рекомендацию по ликвидности для отчета №2

INSERT INTO audit_logs (companies_id, user_id, entity_type, entity_name, action, description)
VALUES
    (1, 1, 'REPORT', 'Отчет1', 'CREATE', 'Загружен новый файл финансовой отчетности за 4 квартал'),
    (1, 2, 'REPORT', 'Отчет2', 'UPDATE', 'Выполнен расчет производных показателей и коэффициентов'),
    (1, 3, 'RECOMMENDATION', 'Рекомендация2', 'DELETE', 'Подтверждена рекомендация по оптимизации себестоимости');