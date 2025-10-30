use tcc_tdah_organizer;
-- Desativa temporariamente as checagens de FK
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tabelas antigas (se existirem)
DROP TABLE IF EXISTS calendar_events;
DROP TABLE IF EXISTS subtasks;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS task_list;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS goals;
DROP TABLE IF EXISTS users;

-- Reativa checagem de FK
SET FOREIGN_KEY_CHECKS = 1;

-- Criando a tabela de usuários
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    theme ENUM('LIGHT','DARK','HIGH_CONTRAST') NOT NULL DEFAULT 'LIGHT',
    default_reminder_minutes INT NOT NULL DEFAULT 15,
    avatar_url VARCHAR(255),
    last_login DATETIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela categories
CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255),
    color VARCHAR(255),
    icon VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_category_user (user_id),
    CONSTRAINT categories_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela task_list
CREATE TABLE task_list (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    color VARCHAR(255),
    icon VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    repetition_interval_amount INT,
    repetition_interval_type VARCHAR(20),
    repeat_when_complete TINYINT(1) DEFAULT 0,
    manual_restart_allowed TINYINT(1) DEFAULT 0,
    deadline TIMESTAMP,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY fk_lists_user_id (user_id),
    CONSTRAINT task_list_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela goals
CREATE TABLE goals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_goals_user (user_id),
    CONSTRAINT goals_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela tasks
CREATE TABLE tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME,
    priority ENUM('LOW','MEDIUM','HIGH') DEFAULT 'MEDIUM',
    status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
    focus_mode TINYINT(1) DEFAULT 0,
    is_done TINYINT(1) DEFAULT 0,
    user_id BIGINT,
    category_id BIGINT,
    goal_id BIGINT,
    task_list_id BIGINT,
    PRIMARY KEY (id),
    KEY idx_tasks_user (user_id),
    KEY idx_tasks_category (category_id),
    KEY idx_tasks_goal (goal_id),
    KEY idx_tasks_list (task_list_id),
    CONSTRAINT tasks_ibfk_1 FOREIGN KEY (task_list_id) REFERENCES task_list(id),
    CONSTRAINT tasks_ibfk_2 FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT tasks_ibfk_3 FOREIGN KEY (goal_id) REFERENCES goals(id),
    CONSTRAINT tasks_ibfk_4 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela subtasks
CREATE TABLE subtasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    task_id BIGINT,
    title VARCHAR(255),
    description VARCHAR(255),
    due_date DATETIME,
    is_done TINYINT(1) DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_subtasks_task (task_id),
    CONSTRAINT subtasks_ibfk_1 FOREIGN KEY (task_id) REFERENCES tasks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela calendar_events
CREATE TABLE calendar_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    timezone VARCHAR(255),
    color VARCHAR(255),
    repetitive TINYINT(1) DEFAULT 0,
    repetition_type ENUM('DAILY','WEEKLY','MONTHLY'),
    repetition_rule VARCHAR(255),
    tactile_reminder TINYINT(1) DEFAULT 0,
    task_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (id),
    KEY idx_calendar_task (task_id),
    KEY idx_calendar_user (user_id),
    CONSTRAINT calendar_events_ibfk_1 FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT calendar_events_ibfk_2 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
