
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    password_salt TEXT NOT NULL,
    user_guid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    login_attempts INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_user_guid ON users (user_guid);

CREATE TABLE IF NOT EXISTS teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    created_by_user_id INTEGER NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teams_created_by_user_id
        FOREIGN KEY(created_by_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_teams_name ON teams (name);
CREATE INDEX IF NOT EXISTS idx_teams_created_by_user_id ON teams (created_by_user_id);

CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE INDEX IF NOT EXISTS idx_roles_name ON roles (name);

CREATE TABLE IF NOT EXISTS team_memberships (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    team_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tm_user_id
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tm_team_id
        FOREIGN KEY(team_id)
        REFERENCES teams(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tm_role_id
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_team_memberships_user_id ON team_memberships (user_id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_team_id ON team_memberships (team_id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_role_id ON team_memberships (role_id);

CREATE TABLE IF NOT EXISTS task_statuses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_task_statuses_name ON task_statuses (name);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    team_id INTEGER NOT NULL, 
    name VARCHAR(255) NOT NULL,
    task_guid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    due_date DATE NOT NULL, 
    assigned_to_user_id INTEGER NOT NULL, 
    CONSTRAINT fk_tasks_user_id
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT, 
    CONSTRAINT fk_tasks_team_id
        FOREIGN KEY(team_id)
        REFERENCES teams(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_tasks_assigned_to_user_id
        FOREIGN KEY(assigned_to_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT 
);

CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks (user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_team_id ON tasks (team_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks (due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to_user_id ON tasks (assigned_to_user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_task_guid ON tasks (task_guid);


CREATE TABLE IF NOT EXISTS task_status_history (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL,
    status_id INTEGER NOT NULL, 
    changed_by_user_id INTEGER NOT NULL, 
    change_timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tsh_task_id
        FOREIGN KEY(task_id)
        REFERENCES tasks(id)
        ON DELETE CASCADE, 
    CONSTRAINT fk_tsh_status_id
        FOREIGN KEY(status_id)
        REFERENCES task_statuses(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_tsh_changed_by_user_id
        FOREIGN KEY(changed_by_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT 
);

CREATE INDEX IF NOT EXISTS idx_tsh_task_timestamp ON task_status_history (task_id, change_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_task_status_history_task_id ON task_status_history (task_id); 
CREATE INDEX IF NOT EXISTS idx_task_status_history_status_id ON task_status_history (status_id);
CREATE INDEX IF NOT EXISTS idx_task_status_history_changed_by_user_id ON task_status_history (changed_by_user_id);
CREATE INDEX IF NOT EXISTS idx_task_status_history_change_timestamp ON task_status_history (change_timestamp);

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER set_tasks_timestamp
BEFORE UPDATE ON tasks
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();


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
