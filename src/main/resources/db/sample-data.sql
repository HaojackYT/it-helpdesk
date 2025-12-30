-- Insert Permissions
INSERT INTO permissions (code, description) VALUES
-- User & Role Management
('USER_CREATE', 'Create user'),
('USER_UPDATE', 'Update user'),
('USER_DELETE', 'Delete user'),
('USER_VIEW', 'View user'),
('ROLE_ASSIGN', 'Assign role'),
('PERMISSION_MANAGE', 'Manage permission'),
-- Ticket Management
('TICKET_CREATE', 'Create ticket'),
('TICKET_VIEW_ALL', 'View all tickets'),
('TICKET_VIEW_OWN', 'View own tickets'),
('TICKET_ASSIGN', 'Assign ticket'),
('TICKET_UPDATE_STATUS', 'Update ticket status'),
('TICKET_CLOSE', 'Close ticket'),
('TICKET_DELETE', 'Delete ticket'),
-- Comment
('COMMENT_ADD', 'Add comment'),
('COMMENT_DELETE', 'Delete comment'),
-- Report
('REPORT_VIEW', 'View report');

-- Insert Roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_IT_SUPPORT'),
('ROLE_EMPLOYEE');

-- Assign Permissions to ROLE_ADMIN (ID: 1)
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions WHERE code IN (
    'USER_CREATE', 'USER_UPDATE', 'USER_DELETE', 'USER_VIEW',
    'ROLE_ASSIGN', 'PERMISSION_MANAGE',
    'TICKET_VIEW_ALL', 'TICKET_ASSIGN', 'TICKET_UPDATE_STATUS', 'TICKET_DELETE',
    'COMMENT_DELETE', 'REPORT_VIEW'
);

-- Assign Permissions to ROLE_IT_SUPPORT (ID: 2)
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions WHERE code IN (
    'TICKET_VIEW_ALL', 'TICKET_ASSIGN', 'TICKET_UPDATE_STATUS',
    'COMMENT_ADD', 'REPORT_VIEW'
);

-- Assign Permissions to ROLE_EMPLOYEE (ID: 3)
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions WHERE code IN (
    'TICKET_CREATE', 'TICKET_VIEW_OWN', 'COMMENT_ADD'
);

-- Insert Users (Password: password123 - BCrypt encoded)
-- Admin User
INSERT INTO users (id, username, password, full_name, email, department, status) VALUES
(UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 'admin', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'System Administrator', 'admin@company.com', 'IT Department', 'ACTIVE');

-- IT Support Users
INSERT INTO users (id, username, password, full_name, email, department, status) VALUES
(UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 'itsupport1', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'John IT Support', 'john.it@company.com', 'IT Department', 'ACTIVE'),
(UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 'itsupport2', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'Jane IT Support', 'jane.it@company.com', 'IT Department', 'ACTIVE');

-- Employee Users
INSERT INTO users (id, username, password, full_name, email, department, status) VALUES
(UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 'employee1', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'Alice Employee', 'alice@company.com', 'Sales', 'ACTIVE'),
(UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 'employee2', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'Bob Employee', 'bob@company.com', 'Marketing', 'ACTIVE'),
(UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 'employee3', '$2a$12$x5ZGyl0sTmSlIupr1ihOFuU3dTQvN8ZXvwVp321PEPx.qQPL2anhe', 'Charlie Employee', 'charlie@company.com', 'HR', 'ACTIVE');

-- Assign Roles to Users
-- Admin gets ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES
(UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 1);

-- IT Support users get ROLE_IT_SUPPORT
INSERT INTO user_roles (user_id, role_id) VALUES
(UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 2),
(UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 2);

-- Employee users get ROLE_EMPLOYEE
INSERT INTO user_roles (user_id, role_id) VALUES
(UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 3),
(UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 3),
(UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 3);

-- Insert Sample Tickets
-- Ticket 1: Open ticket from Alice
INSERT INTO tickets (id, title, description, category, priority, status, created_by, assigned_to) VALUES
(UUID_TO_BIN('aaaa1111-1111-1111-1111-111111111111'), 
 'Cannot connect to VPN', 
 'I am unable to connect to the company VPN from home. Getting error: Connection timeout.', 
 'NETWORK', 'HIGH', 'OPEN', 
 UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), NULL);

-- Ticket 2: Assigned ticket from Bob, assigned to IT Support 1
INSERT INTO tickets (id, title, description, category, priority, status, created_by, assigned_to) VALUES
(UUID_TO_BIN('aaaa2222-2222-2222-2222-222222222222'), 
 'Microsoft Office installation request', 
 'Need Microsoft Office 365 installed on my new laptop.', 
 'SOFTWARE', 'MEDIUM', 'ASSIGNED', 
 UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 
 UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

-- Ticket 3: In Progress ticket from Charlie, assigned to IT Support 2
INSERT INTO tickets (id, title, description, category, priority, status, created_by, assigned_to) VALUES
(UUID_TO_BIN('aaaa3333-3333-3333-3333-333333333333'), 
 'Laptop screen flickering', 
 'My laptop screen has been flickering for the past 2 days. It is affecting my work.', 
 'HARDWARE', 'HIGH', 'IN_PROGRESS', 
 UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 
 UUID_TO_BIN('33333333-3333-3333-3333-333333333333'));

-- Ticket 4: Resolved ticket from Alice
INSERT INTO tickets (id, title, description, category, priority, status, created_by, assigned_to) VALUES
(UUID_TO_BIN('aaaa4444-4444-4444-4444-444444444444'), 
 'Password reset for email account', 
 'I forgot my email password and need it reset.', 
 'ACCOUNT', 'LOW', 'RESOLVED', 
 UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 
 UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

-- Ticket 5: Closed ticket from Bob
INSERT INTO tickets (id, title, description, category, priority, status, created_by, assigned_to) VALUES
(UUID_TO_BIN('aaaa5555-5555-5555-5555-555555555555'), 
 'Printer not working', 
 'The printer on 3rd floor is not printing. Shows offline status.', 
 'HARDWARE', 'MEDIUM', 'CLOSED', 
 UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 
 UUID_TO_BIN('33333333-3333-3333-3333-333333333333'));

-- Insert Sample Comments
-- Comments for Ticket 2 (Office installation)
INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa2222-2222-2222-2222-222222222222'), 
 UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 
 'I will start the installation process today. Please make sure your laptop is connected to the network.');

INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa2222-2222-2222-2222-222222222222'), 
 UUID_TO_BIN('55555555-5555-5555-5555-555555555555'), 
 'Thank you! My laptop is ready.');

-- Comments for Ticket 3 (Screen flickering)
INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa3333-3333-3333-3333-333333333333'), 
 UUID_TO_BIN('33333333-3333-3333-3333-333333333333'), 
 'I have diagnosed the issue. It seems to be a graphics driver problem. Updating the driver now.');

INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa3333-3333-3333-3333-333333333333'), 
 UUID_TO_BIN('66666666-6666-6666-6666-666666666666'), 
 'Thanks for looking into it. Let me know if you need access to my laptop.');

-- Comments for Ticket 4 (Password reset)
INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa4444-4444-4444-4444-444444444444'), 
 UUID_TO_BIN('22222222-2222-2222-2222-222222222222'), 
 'Password has been reset. Please check your phone for the temporary password via SMS.');

INSERT INTO ticket_comments (ticket_id, user_id, content) VALUES
(UUID_TO_BIN('aaaa4444-4444-4444-4444-444444444444'), 
 UUID_TO_BIN('44444444-4444-4444-4444-444444444444'), 
 'Got it! I was able to login successfully. Thank you!');