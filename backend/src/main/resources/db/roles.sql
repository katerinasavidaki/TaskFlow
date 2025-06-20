INSERT INTO roles (id, role_type, name) VALUES
    (1, 'ADMIN', 'Administrator'),
    (2, 'MANAGER', 'Manager'),
    (3, 'TEAM_LEADER', 'Team Leader'),
    (4, 'MEMBER', 'Member');

   ALTER SEQUENCE roles_id_seq RESTART WITH 5;