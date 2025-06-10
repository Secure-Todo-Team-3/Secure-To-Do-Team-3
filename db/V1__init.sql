CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS system_roles (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS team_roles (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);
CREATE INDEX IF NOT EXISTS idx_team_roles_name ON team_roles (name);

CREATE TABLE IF NOT EXISTS task_statuses (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_task_statuses_name ON task_statuses (name);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY, 
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    user_guid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    login_attempts INTEGER NOT NULL DEFAULT 0,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    system_role_id INTEGER NOT NULL,
    is_totp_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    totp_secret TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_system_role_id
        FOREIGN KEY(system_role_id)
        REFERENCES system_roles(id)
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_user_guid ON users (user_guid);

CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    created_by_user_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teams_created_by_user_id
        FOREIGN KEY(created_by_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_teams_name ON teams (name);
CREATE INDEX IF NOT EXISTS idx_teams_created_by_user_id ON teams (created_by_user_id);

CREATE TABLE IF NOT EXISTS team_memberships (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, team_id),
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
        REFERENCES team_roles(id)
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_team_memberships_user_id ON team_memberships (user_id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_team_id ON team_memberships (team_id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_role_id ON team_memberships (role_id);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    creator_user_id BIGINT NOT NULL, 
    team_id BIGINT NOT NULL,
    task_status_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    task_guid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    description TEXT NOT NULL,
    assigned_to_user_id BIGINT NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_creator_user_id 
        FOREIGN KEY(creator_user_id) 
        REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_tasks_team_id
        FOREIGN KEY(team_id)
        REFERENCES teams(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tasks_assigned_to_user_id
        FOREIGN KEY(assigned_to_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_tasks_status_id
        FOREIGN KEY(task_status_id)
        REFERENCES task_statuses(id)
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_tasks_creator_user_id ON tasks (creator_user_id); 
CREATE INDEX IF NOT EXISTS idx_tasks_team_id ON tasks (team_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks (due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to_user_id ON tasks (assigned_to_user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_task_guid ON tasks (task_guid);
CREATE INDEX IF NOT EXISTS idx_tasks_task_status_id ON tasks (task_status_id);

CREATE TABLE IF NOT EXISTS users_history (
    history_id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(10) NOT NULL,
    changed_by_user_id BIGINT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_guid UUID NOT NULL,
    is_locked BOOLEAN NOT NULL,
    is_active BOOLEAN NOT NULL,
    system_role_id INTEGER NOT NULL,
    CONSTRAINT fk_uh_changed_by_user_id FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_users_history_original_id ON users_history(id);

CREATE TABLE IF NOT EXISTS teams_history (
    history_id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(10) NOT NULL,
    changed_by_user_id BIGINT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by_user_id BIGINT NULL,
    is_active BOOLEAN NOT NULL,
    CONSTRAINT fk_th_changed_by_user_id FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_teams_history_original_id ON teams_history(id);

CREATE TABLE IF NOT EXISTS team_memberships_history (
    history_id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(10) NOT NULL,
    changed_by_user_id BIGINT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_tmh_changed_by_user_id FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_team_memberships_history_original_id ON team_memberships_history(id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_history_user_id ON team_memberships_history(user_id);
CREATE INDEX IF NOT EXISTS idx_team_memberships_history_team_id ON team_memberships_history(team_id);

CREATE TABLE IF NOT EXISTS tasks_history (
    history_id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(10) NOT NULL,
    changed_by_user_id BIGINT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id BIGINT NOT NULL,
    creator_user_id BIGINT NOT NULL, 
    team_id BIGINT NOT NULL,
    task_status_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    task_guid UUID NOT NULL,
    description TEXT NOT NULL,
    assigned_to_user_id BIGINT NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    notes TEXT,
    CONSTRAINT fk_tkh_changed_by_user_id FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_tasks_history_original_id ON tasks_history(id);
CREATE INDEX IF NOT EXISTS idx_tasks_history_team_id ON tasks_history(team_id);

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION log_users_history()
RETURNS TRIGGER AS $$
DECLARE
  actor_id BIGINT; actor_id_text TEXT;
BEGIN
  BEGIN
    actor_id_text := current_setting('audit.current_user_id', true);
    IF actor_id_text IS NOT NULL AND actor_id_text <> '' THEN actor_id := actor_id_text::BIGINT; ELSE actor_id := NULL; END IF;
  EXCEPTION WHEN OTHERS THEN actor_id := NULL; END;
  IF (TG_OP = 'UPDATE' OR TG_OP = 'DELETE') THEN
    INSERT INTO users_history (operation_type, changed_by_user_id, id, username, email, user_guid, is_locked, is_active, system_role_id)
    VALUES (TG_OP, actor_id, OLD.id, OLD.username, OLD.email, OLD.user_guid, OLD.is_locked, OLD.is_active, OLD.system_role_id);
  END IF; RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION log_teams_history()
RETURNS TRIGGER AS $$
DECLARE
  actor_id BIGINT; actor_id_text TEXT;
BEGIN
  BEGIN
    actor_id_text := current_setting('audit.current_user_id', true);
    IF actor_id_text IS NOT NULL AND actor_id_text <> '' THEN actor_id := actor_id_text::BIGINT; ELSE actor_id := NULL; END IF;
  EXCEPTION WHEN OTHERS THEN actor_id := NULL; END;
  IF (TG_OP = 'UPDATE' OR TG_OP = 'DELETE') THEN
    INSERT INTO teams_history (operation_type, changed_by_user_id, id, name, description, created_by_user_id, is_active)
    VALUES (TG_OP, actor_id, OLD.id, OLD.name, OLD.description, OLD.created_by_user_id, OLD.is_active);
  END IF; RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION log_team_memberships_history()
RETURNS TRIGGER AS $$
DECLARE
  actor_id BIGINT; actor_id_text TEXT;
BEGIN
  BEGIN
    actor_id_text := current_setting('audit.current_user_id', true);
    IF actor_id_text IS NOT NULL AND actor_id_text <> '' THEN actor_id := actor_id_text::BIGINT; ELSE actor_id := NULL; END IF;
  EXCEPTION WHEN OTHERS THEN actor_id := NULL; END;
  IF (TG_OP = 'UPDATE' OR TG_OP = 'DELETE') THEN
    INSERT INTO team_memberships_history (operation_type, changed_by_user_id, id, user_id, team_id, role_id, assigned_at)
    VALUES (TG_OP, actor_id, OLD.id, OLD.user_id, OLD.team_id, OLD.role_id, OLD.assigned_at);
  ELSIF (TG_OP = 'INSERT') THEN
    INSERT INTO team_memberships_history (operation_type, changed_by_user_id, id, user_id, team_id, role_id, assigned_at)
    VALUES (TG_OP, actor_id, NEW.id, NEW.user_id, NEW.team_id, NEW.role_id, NEW.assigned_at);
  END IF; RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION log_tasks_history()
RETURNS TRIGGER AS $$
DECLARE
  actor_id BIGINT; actor_id_text TEXT;
BEGIN
  BEGIN
    actor_id_text := current_setting('audit.current_user_id', true);
    IF actor_id_text IS NOT NULL AND actor_id_text <> '' THEN actor_id := actor_id_text::BIGINT; ELSE actor_id := NULL; END IF;
  EXCEPTION WHEN OTHERS THEN actor_id := NULL; END;
  IF (TG_OP = 'UPDATE' OR TG_OP = 'DELETE') THEN
    INSERT INTO tasks_history (operation_type, changed_by_user_id, id, creator_user_id, team_id, task_status_id, name, task_guid, description, assigned_to_user_id, due_date, created_at, updated_at) -- UPDATED
    VALUES (TG_OP, actor_id, OLD.id, OLD.creator_user_id, OLD.team_id, OLD.task_status_id, OLD.name, OLD.task_guid, OLD.description, OLD.assigned_to_user_id, OLD.due_date, OLD.created_at, OLD.updated_at); -- UPDATED
  END IF; RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_tasks_timestamp BEFORE UPDATE ON tasks FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER users_history_trigger AFTER UPDATE OR DELETE ON users FOR EACH ROW EXECUTE FUNCTION log_users_history();
CREATE TRIGGER teams_history_trigger AFTER UPDATE OR DELETE ON teams FOR EACH ROW EXECUTE FUNCTION log_teams_history();
CREATE TRIGGER team_memberships_history_trigger AFTER INSERT OR UPDATE OR DELETE ON team_memberships FOR EACH ROW EXECUTE FUNCTION log_team_memberships_history();
CREATE TRIGGER tasks_history_trigger AFTER UPDATE OR DELETE ON tasks FOR EACH ROW EXECUTE FUNCTION log_tasks_history();