INSERT INTO system_roles (name, description) VALUES
    ('SYSTEM_ADMIN', 'System administrator with full control over all data.'),
    ('REGULAR_USER', 'A standard user with abilities to join teams and manage tasks.')
ON CONFLICT (name) DO NOTHING;

INSERT INTO team_roles (name, description) VALUES
    ('Admin', 'Team administrator with full control over the team, its members, and tasks.'),
    ('Team Lead', 'Manages a specific team and its members/tasks.'),
    ('Member', 'Regular team member, can manage their tasks and team tasks based on permissions.')
ON CONFLICT (name) DO NOTHING;

INSERT INTO task_statuses (name, description) VALUES
    ('To Do', 'Task has not been started.'),
    ('In Progress', 'Task is actively being worked on.'),
    ('Blocked', 'Task is impeded.'),
    ('In Review', 'Task is completed and awaiting review.'),
    ('Done', 'Task has been successfully completed.'),
    ('Archived', 'Task is completed and archived.'),
    ('Cancelled', 'Task has been cancelled.')
ON CONFLICT (name) DO NOTHING;