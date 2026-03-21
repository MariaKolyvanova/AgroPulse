-- 1. Компании
INSERT INTO companies (name)
VALUES ('Русагро'), ('Степь');

-- 2. Пользователи (username, firstname, lastname)
INSERT INTO users (username, password, firstname, lastname, role, company_id)
VALUES ('m_kolyvanova', '{noop}password123', 'Мария', 'Колыванова', 'ADMIN', 1),
       ('ivan_analyst', '{noop}password456', 'Иван', 'Петров', 'ANALYST', 1);

-- 3. Показатели (Indicators)
INSERT INTO indicators (name, code, category, formula, normative_value)
VALUES ('Выбросы парниковых газов', 'ESG_ENV_01', 'ENVIRONMENTAL', 'CO2 / Production', 100.5),
       ('Доля локальных поставщиков', 'ESG_SOC_05', 'SOCIAL', 'Local_Suppliers / Total_Suppliers', 50.0),
       ('Рентабельность активов (ROA)', 'FIN_ROA', 'FINANCIAL', 'Net_Income / Total_Assets', 15.0);

-- 4. Рекомендации (Recommendations)
INSERT INTO recommendations (indicator_id, title, text_content, priority, condition_rule)
VALUES (1, 'Переход на биогаз', 'Рекомендуется внедрение биогазовых установок для переработки отходов.', 'HIGH', 'value > normative'),
       (2, 'Поддержка фермерства', 'Увеличить квоты на закупку у местных КФХ.', 'LOW', 'value < normative');

-- 5. Отчеты (FinancialReports)
INSERT INTO financial_reports (name, type_file, period_year, status, company_id)
VALUES ('Отчет по устойчивому развитию 2025', 'XLSX', 2025, 'ON_VERIFICATION', 1);

-- 6. Сырые данные (RawData)
INSERT INTO raw_data (report_id, row_index, cell_address, raw_value, indicator_id, is_valid)
VALUES (1, 5, 'B10', '125.4', 1, TRUE),  -- Превышение по выбросам
       (1, 12, 'C4', '45.0', 2, TRUE);   -- Немного ниже нормы по поставщикам

-- 7. Значения показателей (IndicatorValues) - после обработки RawData
INSERT INTO indicator_values (report_id, indicator_id, value)
VALUES (1, 1, 125.4),
       (1, 2, 45.0);

-- 8. Результаты анализа (AnalysisResults)
INSERT INTO analysis_results (report_id, analyst_id, total_score, comment)
VALUES (1, 2, 65.8, 'Требуется корректировка экологической стратегии.');

-- 9. Экспорт (ReportExports)
INSERT INTO report_exports (analysis_id, user_id, file_path, export_format, file_size)
VALUES (1, 1, '/exports/reports/report_2025_id1.pdf', 'PDF', 1024500);

-- 10. Логи (AuditLogs)
INSERT INTO audit_logs (user_id, action, entity_name, entity_id)
VALUES (1, 'CREATE_REPORT', 'financial_reports', 1),
       (2, 'CALCULATE_ANALYSIS', 'analysis_results', 1);