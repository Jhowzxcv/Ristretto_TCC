  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE tcc_tdah_organizer;

-- Tabela de usuários
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL COMMENT 'Senha criptografada com BCrypt',
    theme ENUM('light', 'dark', 'high_contrast') DEFAULT 'light',
    default_reminder_minutes INT DEFAULT 15,
    avatar_url VARCHAR(255) COMMENT 'URL da imagem de perfil',
    last_login DATETIME COMMENT 'Data do último acesso',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabela de categorias
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    color VARCHAR(7) DEFAULT '#FF9F89',
    icon VARCHAR(30) COMMENT 'Ícone FontAwesome (ex: fa-book)',
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_category_user (user_id)
) ENGINE=InnoDB;

-- Tabela de metas
CREATE TABLE goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    creation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_goal_user (user_id)
) ENGINE=InnoDB;

-- Tabela de tarefas
CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    creation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME,
    priority ENUM('low', 'medium', 'high') DEFAULT 'medium',
    status ENUM('not_started', 'in_progress', 'completed') DEFAULT 'not_started',
    user_id INT NOT NULL,
    category_id INT,
    goal_id INT,
    focus_mode BOOLEAN DEFAULT FALSE,
    is_done BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE SET NULL,
    INDEX idx_task_user (user_id),
    INDEX idx_task_due_date (due_date),
    INDEX idx_task_status (status)
) ENGINE=InnoDB;

-- Tabela de subtarefas
CREATE TABLE subtasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    is_done BOOLEAN DEFAULT FALSE,
    description TEXT,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabela de eventos no calendário
CREATE TABLE calendar_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    timezone VARCHAR(50) COMMENT 'Ex: America/Sao_Paulo',
    color VARCHAR(7) DEFAULT '#4CAF50',
    task_id INT,
    user_id INT NOT NULL,
    repetitive BOOLEAN DEFAULT FALSE,
    repetition_type ENUM('daily', 'weekly', 'monthly'),
    repetition_rule VARCHAR(100) COMMENT 'Formato RRULE (RFC 5545)',
    tactile_reminder BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL,
    INDEX idx_event_user (user_id),
    INDEX idx_event_date_range (start_date, end_date)
) ENGINE=InnoDB;

-- Índices adicionais para otimização
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_events_repetitive ON calendar_events(repetitive);