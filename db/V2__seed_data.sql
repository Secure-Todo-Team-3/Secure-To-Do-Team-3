
INSERT INTO roles (name, description) VALUES
    ('Admin', 'System administrator with full control.'),
    ('Team Lead', 'Manages a specific team and its members/tasks.'),
    ('Member', 'Regular user, can manage their tasks and team tasks based on permissions.')
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
