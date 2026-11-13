CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    session_version INTEGER NOT NULL DEFAULT 0,
    password_change_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS portals (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(120) NOT NULL,
    subtitle VARCHAR(300) NOT NULL,
    welcome_label VARCHAR(80) NOT NULL DEFAULT 'Welcome to',
    layout VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS hub_links (
    id BIGSERIAL PRIMARY KEY,
    portal_id BIGINT NOT NULL REFERENCES portals (id) ON DELETE CASCADE,
    name VARCHAR(120) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    description VARCHAR(300) NOT NULL,
    icon VARCHAR(80) NOT NULL,
    sort_order INTEGER NOT NULL,
    download_name VARCHAR(200),
    network_only BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    click_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (portal_id, name)
);

CREATE INDEX IF NOT EXISTS idx_hub_links_portal_sort ON hub_links (portal_id, sort_order);

INSERT INTO portals (code, title, subtitle, welcome_label, layout)
VALUES
    ('ADMIN', 'Admin Portal', 'Access your essential administrative services in one place.', 'Welcome to', 'admin'),
    ('LEARNER', 'Learner''s Hub', 'Access your essential student services in one place.', 'Welcome to', 'learner')
ON CONFLICT (code) DO NOTHING;

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'MIS Helpdesk', 'https://helpdesk.lpulaguna.com', 'Support and concerns', 'MIS Helpdesk', 10, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'MIS Helpdesk');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'LMS', 'https://lms.lpulaguna.edu.ph', 'Learning Management System', 'LMS', 20, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'LMS');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Office 365', 'https://office.com', 'Email and productivity tools', 'Office 365', 30, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'Office 365');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Outlook', 'https://outlook.office.com/', 'Email and calendar', 'Outlook', 40, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'Outlook');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'MS Teams', 'https://teams.microsoft.com/', 'Meetings and collaboration', 'MS Teams', 50, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'MS Teams');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'ERP', 'https://erp.lpulaguna.edu.ph', 'Enterprise resource planning', 'ERP', 60, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'ERP');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Dorado (Payslips)', 'https://dorado.grantthorntonsolutions.ph/1.0.0.17871/Account/Login', 'Grant Thornton Dorado', 'Dorado (Payslips)', 70, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'Dorado (Payslips)');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Reservation System', 'https://reservation.lpulaguna.com', 'Book rooms and facilities', 'Reservation System', 80, NULL, FALSE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'Reservation System');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Internet Access', 'http://web.lpu-laguna.edu.ph:8090', 'LPU Captive Portal', 'Internet Access', 90, NULL, TRUE
FROM portals WHERE code = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'ADMIN' AND l.name = 'Internet Access');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'MIS Helpdesk', 'https://helpdesk.lpulaguna.com', 'Support and concerns', 'MIS Helpdesk', 10, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'MIS Helpdesk');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'LMS', 'https://lms.lpulaguna.edu.ph', 'Learning Management System', 'LMS', 20, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'LMS');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Office 365', 'https://office.com', 'Email and productivity tools', 'Office 365', 30, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'Office 365');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Outlook', 'https://outlook.office.com/', 'Email and calendar', 'Outlook', 40, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'Outlook');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'MS Teams', 'https://teams.microsoft.com/', 'Meetings and collaboration', 'MS Teams', 50, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'MS Teams');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Student Portal', 'https://students.lpulaguna.edu.ph/Student/Login.php', 'Grades, records, and profile', 'Student Portal', 60, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'Student Portal');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Reservation System', 'https://learnerreservation.lpulaguna.com', 'Book rooms and facilities', 'Reservation System', 70, NULL, FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'Reservation System');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'Internet Access', 'http://web.lpu-laguna.edu.ph:8090', 'LPU Captive Portal', 'Internet Access', 80, NULL, TRUE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'Internet Access');

INSERT INTO hub_links (portal_id, name, url, description, icon, sort_order, download_name, network_only)
SELECT id, 'LIT', '/LIT_LPU_V5.apk', 'Download LIT Android app', 'LIT', 90, 'LIT_LPU_V5.apk', FALSE
FROM portals WHERE code = 'LEARNER'
AND NOT EXISTS (SELECT 1 FROM hub_links l JOIN portals p ON p.id = l.portal_id WHERE p.code = 'LEARNER' AND l.name = 'LIT');
