--
-- Dasient Integration Project - Database Setup
--
-- Run this once after the application started and created the table structure.
--

INSERT INTO rule_action (rule_action_id, display_name) VALUES ('ignore', 'Ignore');
INSERT INTO rule_action (rule_action_id, display_name) VALUES ('warn', 'Warn');
INSERT INTO rule_action (rule_action_id, display_name) VALUES ('clean', 'Clean');
INSERT INTO rule_action (rule_action_id, display_name) VALUES ('quarantine', 'Quarantine');

INSERT INTO revision_type (revision_type_id, display_name) VALUES ('CREATE', 'Create');
INSERT INTO revision_type (revision_type_id, display_name) VALUES ('UPDATE', 'Update');
INSERT INTO revision_type (revision_type_id, display_name) VALUES ('DELETE', 'Delete');
INSERT INTO revision_type (revision_type_id, display_name) VALUES ('RESURRECT', 'Resurrect');

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (1, 'cp.opentransfer.com', 'pgsqlcp.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (2, 'cp2.opentransfer.com', 'pgsqlcp2.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (3, 'cp3.opentransfer.com', 'pgsqlcp3.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (4, 'cp4.opentransfer.com', 'pgsqlcp4.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (5, 'cp5.opentransfer.com', 'pgsqlcp5.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (6, 'cp6.opentransfer.com', 'pgsqlcp6.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (7, 'cp7.opentransfer.com', 'pgsqlcp7.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (8, 'cp8.opentransfer.com', 'pgsqlcp8.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (9, 'cp9.opentransfer.com', 'pgsqlcp9.opentransfer.com', false, 0);

INSERT INTO control_panel (control_panel_id, name, db_host, sync_enabled, last_synced_domain_id)
    VALUES (10, 'cp10.opentransfer.com', 'pgsqlcp10.opentransfer.com', false, 0);

-- Indexes on foreign keys

CREATE INDEX clean_file_clean_history_id_idx
    ON clean_file (clean_history_id);

CREATE INDEX clean_history_revision_id_idx
    ON clean_history (revision_id);

CREATE INDEX clean_history_web_server_id_idx
    ON clean_history (web_server_id);

CREATE INDEX clean_history_control_panel_id_idx
    ON clean_history (control_panel_id);

CREATE INDEX clean_history_triggers_clean_history_id_idx
    ON clean_history_triggers (clean_history_id);

CREATE INDEX clean_rule_match_clean_file_id_idx
    ON clean_rule_match (clean_file_id);

CREATE INDEX clean_rule_match_rule_revision_id_idx
    ON clean_rule_match (rule_revision_id);

CREATE INDEX failure_scan_history_id_idx
    ON failure (scan_history_id);

CREATE INDEX malicious_source_code_snippet_malicious_url_id_idx
    ON malicious_source_code_snippet (malicious_url_id);

CREATE INDEX malicious_url_scan_history_id_idx
    ON malicious_url (scan_history_id);

CREATE INDEX on_blacklist_scan_history_id_idx
    ON on_blacklist (scan_history_id);

CREATE INDEX pending_clean_web_server_id_idx
    ON pending_clean (web_server_id);

CREATE INDEX pending_clean_control_panel_id_idx
    ON pending_clean (control_panel_id);

CREATE INDEX pending_clean_triggers_pending_clean_id_idx
    ON pending_clean_triggers (pending_clean_id);

CREATE INDEX request_ignored_scan_history_id_idx
    ON request_ignored (scan_history_id);

CREATE INDEX rule_revision_type_id_idx
    ON rule_revision (type_id);

CREATE INDEX rule_revision_rule_id_idx
    ON rule_revision (rule_id);

CREATE INDEX rule_revision_state_id_idx
    ON rule_revision (state_id);

CREATE INDEX rule_state_action_id_idx
    ON rule_state (action_id);

CREATE INDEX rule_term_rule_state_id_idx
    ON rule_term (rule_state_id);

CREATE INDEX scanned_url_scan_history_id_idx
    ON scanned_url (scan_history_id);

CREATE INDEX suspicious_source_code_snippet_suspicious_url_id_idx
    ON suspicious_source_code_snippet (suspicious_url_id);

CREATE INDEX suspicious_url_scan_history_id_idx
    ON suspicious_url (scan_history_id);

CREATE INDEX web_server_control_panel_id_idx
    ON web_server (control_panel_id);

CREATE INDEX web_server_activity_web_server_id_idx
    ON web_server_activity (web_server_id);

CREATE INDEX web_server_storage_web_storage_id_idx
    ON web_server_storage (web_storage_id);

-- Defaults on identity columns

ALTER TABLE clean_file
    ALTER COLUMN clean_file_id
    SET DEFAULT NEXTVAL('clean_file_seq');

SELECT SETVAL('clean_file_seq', (SELECT
    COALESCE(MAX(clean_file_id) + 1, 1)
    FROM clean_file));

ALTER TABLE clean_rule_match
    ALTER COLUMN clean_rule_match_id
    SET DEFAULT NEXTVAL('clean_rule_match_seq');

SELECT SETVAL('clean_rule_match_seq', (SELECT
    COALESCE(MAX(clean_rule_match_id) + 1, 1)
    FROM clean_rule_match));

ALTER TABLE domain
    ALTER COLUMN domain_id
    SET DEFAULT NEXTVAL('domain_seq');

SELECT SETVAL('domain_seq', (SELECT
    COALESCE(MAX(domain_id) + 1, 1)
    FROM domain));

ALTER TABLE failure
    ALTER COLUMN failure_id
    SET DEFAULT NEXTVAL('failure_seq');

SELECT SETVAL('failure_seq', (SELECT
    COALESCE(MAX(failure_id) + 1, 1)
    FROM failure));

ALTER TABLE malicious_source_code_snippet
    ALTER COLUMN snippet_id
    SET DEFAULT NEXTVAL('malicious_source_code_snippet_seq');

SELECT SETVAL('malicious_source_code_snippet_seq', (SELECT
    COALESCE(MAX(snippet_id) + 1, 1)
    FROM malicious_source_code_snippet));

ALTER TABLE malicious_url
    ALTER COLUMN malicious_url_id
    SET DEFAULT NEXTVAL('malicious_url_seq');

SELECT SETVAL('malicious_url_seq', (SELECT
    COALESCE(MAX(malicious_url_id) + 1, 1)
    FROM malicious_url));

ALTER TABLE on_blacklist
    ALTER COLUMN on_blacklist_id
    SET DEFAULT NEXTVAL('on_blacklist_seq');

SELECT SETVAL('on_blacklist_seq', (SELECT
    COALESCE(MAX(on_blacklist_id) + 1, 1)
    FROM on_blacklist));

ALTER TABLE pending_clean
    ALTER COLUMN pending_clean_id
    SET DEFAULT NEXTVAL('clean_history_seq');

SELECT SETVAL('clean_history_seq', (SELECT
    COALESCE(MAX(pending_clean_id) + 1, 1)
    FROM pending_clean));

ALTER TABLE request_ignored
    ALTER COLUMN request_ignored_id
    SET DEFAULT NEXTVAL('request_ignored_seq');

SELECT SETVAL('request_ignored_seq', (SELECT
    COALESCE(MAX(request_ignored_id) + 1, 1)
    FROM request_ignored));

ALTER TABLE revision
    ALTER COLUMN revision_id
    SET DEFAULT NEXTVAL('revision_seq');

SELECT SETVAL('revision_seq', (SELECT
    COALESCE(MAX(revision_id) + 1, 1)
    FROM revision));

ALTER TABLE rule
    ALTER COLUMN rule_id
    SET DEFAULT NEXTVAL('rule_seq');

SELECT SETVAL('rule_seq', (SELECT
    COALESCE(MAX(rule_id) + 1, 1)
    FROM rule));

ALTER TABLE rule_revision
    ALTER COLUMN rule_revision_id
    SET DEFAULT NEXTVAL('rule_revision_seq');

SELECT SETVAL('rule_revision_seq', (SELECT
    COALESCE(MAX(rule_revision_id) + 1, 1)
    FROM rule_revision));

ALTER TABLE rule_state
    ALTER COLUMN rule_state_id
    SET DEFAULT NEXTVAL('rule_state_seq');

SELECT SETVAL('rule_state_seq', (SELECT
    COALESCE(MAX(rule_state_id) + 1, 1)
    FROM rule_state));

ALTER TABLE rule_term
    ALTER COLUMN rule_term_id
    SET DEFAULT NEXTVAL('rule_term_seq');

SELECT SETVAL('rule_term_seq', (SELECT
    COALESCE(MAX(rule_term_id) + 1, 1)
    FROM rule_term));

ALTER TABLE scan_history
    ALTER COLUMN scan_history_id
    SET DEFAULT NEXTVAL('scan_history_seq');

SELECT SETVAL('scan_history_seq', (SELECT
    COALESCE(MAX(scan_history_id) + 1, 1)
    FROM scan_history));

ALTER TABLE scanned_url
    ALTER COLUMN scanned_url_id
    SET DEFAULT NEXTVAL('scanned_url_seq');

SELECT SETVAL('scanned_url_seq', (SELECT
    COALESCE(MAX(scanned_url_id) + 1, 1)
    FROM scanned_url));

ALTER TABLE suspicious_source_code_snippet
    ALTER COLUMN snippet_id
    SET DEFAULT NEXTVAL('suspicious_source_code_snippet_seq');

SELECT SETVAL('suspicious_source_code_snippet_seq', (SELECT
    COALESCE(MAX(snippet_id) + 1, 1)
    FROM suspicious_source_code_snippet));

ALTER TABLE suspicious_url
    ALTER COLUMN suspicious_url_id
    SET DEFAULT NEXTVAL('suspicious_url_seq');

SELECT SETVAL('suspicious_url_seq', (SELECT
    COALESCE(MAX(suspicious_url_id) + 1, 1)
    FROM suspicious_url));

ALTER TABLE web_server
    ALTER COLUMN web_server_id
    SET DEFAULT NEXTVAL('web_server_seq');

SELECT SETVAL('web_server_seq', (SELECT
    COALESCE(MAX(web_server_id) + 1, 1)
    FROM web_server));

ALTER TABLE web_server_activity
    ALTER COLUMN web_server_activity_id
    SET DEFAULT NEXTVAL('web_server_activity_seq');

SELECT SETVAL('web_server_seq', (SELECT
    COALESCE(MAX(web_server_id) + 1, 1)
    FROM web_server_activity));

ALTER TABLE web_storage
    ALTER COLUMN web_storage_id
    SET DEFAULT NEXTVAL('web_storage_seq');

SELECT SETVAL('web_storage_seq', (SELECT
    COALESCE(MAX(web_storage_id) + 1, 1)
    FROM web_storage));

-- Various indexes to speed up frequent lookups

CREATE INDEX domain_name_idx
    ON domain (name);

-- CREATE INDEX domain_status_idx
--    ON domain (status);

--CREATE INDEX domain_to_scan_idx1
--    ON domain (domain_id)
--    WHERE NOT is_deleted AND istatus = 'SCAN_NOW';

--CREATE INDEX domain_to_scan_idx2
--    ON domain (next_scan)
--    WHERE NOT is_deleted AND istatus = 'NEW';

CREATE INDEX revision_time_idx
    ON revision (revision_time);

CREATE INDEX revision_author_idx
    ON revision (revision_author);

CREATE INDEX rule_revision_live_rules_idx
    ON rule_revision (rule_revision_id)
    WHERE NOT is_deleted;

CREATE INDEX web_server_activity_access_time_idx
    ON web_server_activity (access_time);
