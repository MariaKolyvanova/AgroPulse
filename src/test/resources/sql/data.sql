INSERT INTO companies (name, region, industry)
VALUES ('АО МПБК Очаково', 'Москва', 'Пищевая промышленность'),
       ('ООО Южный Виноградник', 'Краснодарский край', 'Сельское хозяйство');

INSERT INTO users (username, password, first_name, last_name, company_id, role)
VALUES ('admin_och', 'hash1', 'Иван', 'Иванов', 1, 'ADMIN'),
       ('analyst_och', 'hash2', 'Петр', 'Петров', 1, 'ANALYST'),
       ('op_och', 'hash3', 'Анна', 'Сидорова', 1, 'OPERATOR');

INSERT INTO users (username, password, first_name, last_name, company_id, role)
VALUES ('admin_uv', 'hash4', 'Сергей', 'Кузнецов', 2, 'ADMIN'),
       ('analyst_uv', 'hash5', 'Елена', 'Васильева', 2, 'ANALYST'),
       ('op_uv', 'hash6', 'Ольга', 'Морозова', 2, 'OPERATOR');

INSERT INTO indicators (name, code, category, formula, normative_value)
VALUES ('Коэффициент текущей ликвидности', 'CTL', 'Ликвидность', 'Оборотные активы / Краткосрочные обязательства', 2.0),
       ('Рентабельность продаж', 'ROS', 'Рентабельность', '(Чистая прибыль / Выручка) * 100', 0.15),
       ('Коэффициент автономии', 'CA', 'Финансовая устойчивость', 'Собственный капитал / Активы', 0.5);

INSERT INTO recommendations (indicator_id, title, description, condition_rule)
VALUES (1, 'Низкая ликвидность', 'Необходимо оптимизировать дебиторскую задолженность.', 'value < 1.5'),
       (2, 'Падение рентабельности', 'Рекомендуется пересмотреть стратегию ценообразования.', 'value < 0.10');

INSERT INTO initial_data (company_id, name, type_file, period, status, file_path, file_size)
VALUES (1, 'Баланс_2023.xlsx', 'XLSX', '2023', 'APPROVED', '/storage/1/balans_2023.xlsx', 1024),
       (2, 'Отчет_ЮГ_2023.csv', 'CSV', '2023', 'APPROVED', '/storage/2/report_uv_2023.csv', 512);

INSERT INTO raw_data (report_id, indicator_id, row_index, cell_address, value, is_valid)
VALUES (1, 1, 10, 'B10', '1.8', TRUE),
       (1, 2, 15, 'C15', '0.12', TRUE),
       (2, 1, 5, 'A5', '2.2', TRUE);

INSERT INTO indicator_values (report_id, indicator_id, value)
VALUES (1, 1, 1.8),
       (1, 2, 0.12),
       (2, 1, 2.2);

INSERT INTO analysis_results (report_id, user_id, comment)
VALUES (1, 2, 'Требуется внимание к ликвидности предприятия.'),
       (2, 5, 'Показатели в пределах нормы.');

INSERT INTO accepted_recommendations (analysis_results_id, recommendation_id, description, status)
VALUES (1, 1, 'Необходимо оптимизировать дебиторскую задолженность.', 'ACCEPTED'),
       (1, 2, 'Рекомендуется пересмотреть стратегию ценообразования.', 'REJECTED');

INSERT INTO report_exports (analysis_results_id, export_format, file_name, file_path, file_size)
VALUES (1, 'PDF', 'Анализ_Очаково_2023.pdf', '/exports/anal_och_2023.pdf', 2048);

INSERT INTO audit_logs (company_id, user_id, user_name, action, description)
VALUES (1, 1, 'Иван Иванов', 'LOGIN', 'Вход в систему'),
       (1, 3, 'Анна Сидорова', 'UPLOAD', 'Загружен файл Баланс_2023.xlsx');