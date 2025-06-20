INSERT INTO task_priorities (id, priority_type, name) VALUES
    (1, 'LOW', 'Low'),
    (2, 'MEDIUM', 'Medium'),
    (3, 'HIGH', 'High'),
    (4, 'URGENT', 'Urgent');

    ALTER SEQUENCE task_priorities_id_seq RESTART WITH 5;