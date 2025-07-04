
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    theme VARCHAR(20),
    default_reminder_minutes INT,
    avatar_url VARCHAR(255),
    last_login DATETIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

CREATE TABLE goals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    creation_date DATETIME NOT NULL,
    due_date DATETIME,
    user_id INT NOT NULL
);

CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    color VARCHAR(7),
    icon VARCHAR(30),
    user_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    creation_date DATETIME NOT NULL,
    due_date DATETIME,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id INT NOT NULL,
    category_id INT,
    goal_id INT,
    focus_mode TINYINT(1) DEFAULT 0,
    is_done TINYINT(1) DEFAULT 0
);

CREATE TABLE subtasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_task INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    is_done TINYINT(1) DEFAULT 0,
    description TEXT
);

CREATE TABLE calendar_events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    timezone VARCHAR(50),
    color VARCHAR(7),
    task_id INT,
    user_id INT NOT NULL,
    goal_id INT,
    repetitive TINYINT(1) DEFAULT 0,
    repetition_type VARCHAR(20),
    repetition_rule VARCHAR(100),
    tactile_reminder TINYINT(1) DEFAULT 0
);
