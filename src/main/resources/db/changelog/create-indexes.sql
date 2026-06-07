--liquibase formatted sql

--changeset MariaKolyvanova:indexes-users
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users (company_id);
CREATE INDEX IF NOT EXISTS idx_users_company_id_user_status ON users (company_id, user_status);

--changeset MariaKolyvanova:indexes-reports
CREATE INDEX IF NOT EXISTS idx_reports_company_id ON reports (company_id);
CREATE INDEX IF NOT EXISTS idx_reports_company_id_created_at ON reports (company_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reports_company_id_year_month ON reports (company_id, report_year, report_month);

--changeset MariaKolyvanova:indexes-report-audit
CREATE INDEX IF NOT EXISTS idx_report_audit_company_id_date ON report_audit (company_id, date DESC);
CREATE INDEX IF NOT EXISTS idx_report_audit_report_id_company_id_date ON report_audit (report_id, company_id, date DESC);

--changeset MariaKolyvanova:indexes-auth-audit
CREATE INDEX IF NOT EXISTS idx_auth_audit_company_id_auth_at ON auth_audit (company_id, auth_at DESC);

--changeset MariaKolyvanova:indexes-values-indicators
CREATE INDEX IF NOT EXISTS idx_vifr_report_id ON values_indicators_financial_reports (report_id);
CREATE INDEX IF NOT EXISTS idx_vifr_report_id_indicator_id ON values_indicators_financial_reports (report_id, indicator_id);

CREATE INDEX IF NOT EXISTS idx_vifa_report_id ON values_indicators_financial_analyze (report_id);

CREATE INDEX IF NOT EXISTS idx_vie_company_year ON values_indicators_esg (company_id, report_year);

--changeset MariaKolyvanova:indexes-analytics
CREATE INDEX IF NOT EXISTS idx_report_comments_report_id ON report_comments (report_id);

--changeset MariaKolyvanova:indexes-recommendations
CREATE INDEX IF NOT EXISTS idx_recommendations_indicator_id ON recommendations (indicator_id);
CREATE INDEX IF NOT EXISTS idx_recommendations_esg_indicator_id ON recommendations_esg (indicator_id);

--changeset MariaKolyvanova:indexes-indicators-financial-reports
CREATE INDEX IF NOT EXISTS idx_indicators_financial_reports_xml_tag ON indicators_financial_reports (xml_tag);

--changeset MariaKolyvanova:indexes-envers
CREATE INDEX IF NOT EXISTS idx_revinfo_username ON revinfo (username);
CREATE INDEX IF NOT EXISTS idx_revinfo_revtstmp ON revinfo (revtstmp DESC);
CREATE INDEX IF NOT EXISTS idx_users_aud_rev ON users_aud (rev);
