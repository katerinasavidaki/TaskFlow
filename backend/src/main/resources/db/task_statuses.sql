INSERT INTO task_statuses (id, status_type, name) VALUES
    (1, 'TODO', 'To Do'),
    (2, 'IN_PROGRESS', 'In Progress'),
    (3, 'REVIEW', 'In Review'),
    (4, 'COMPLETED', 'Completed');

    ALTER SEQUENCE task_statuses_id_seq RESTART WITH 5;