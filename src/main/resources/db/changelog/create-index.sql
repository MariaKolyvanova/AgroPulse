CREATE INDEX idx_audit_logs_user_id
    ON audit_logs(user_id);

CREATE INDEX idx_audit_logs_entity_type
    ON audit_logs(entity_type);

CREATE INDEX idx_audit_logs_entity_name
    ON audit_logs(entity_name);

CREATE INDEX idx_audit_logs_created_at
    ON audit_logs(created_at);