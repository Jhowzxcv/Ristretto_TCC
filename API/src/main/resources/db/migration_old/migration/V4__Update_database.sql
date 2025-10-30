-- Garantir que o banco está sendo usado
USE tcc_tdah_organizer;

-- Etapa 1: Ajustar o charset e collation das tabelas para utf8mb4
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE categories CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE goals CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE tasks CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE subtasks CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE calendar_events CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Etapa 2: Atualizar valores NULL para valores padrão antes do ALTER
UPDATE users SET theme = 'LIGHT' WHERE theme IS NULL;
UPDATE users SET default_reminder_minutes = 15 WHERE default_reminder_minutes IS NULL;
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

UPDATE categories SET color = '#FF9F89' WHERE color IS NULL;
UPDATE categories SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

UPDATE goals SET creation_date = CURRENT_TIMESTAMP WHERE creation_date IS NULL;

UPDATE tasks SET priority = 'MEDIUM' WHERE priority IS NULL;
UPDATE tasks SET status = 'NOT_STARTED' WHERE status IS NULL;
UPDATE tasks SET creation_date = CURRENT_TIMESTAMP WHERE creation_date IS NULL;
UPDATE tasks SET focus_mode = FALSE WHERE focus_mode IS NULL;
UPDATE tasks SET is_done = FALSE WHERE is_done IS NULL;

UPDATE subtasks SET is_done = FALSE WHERE is_done IS NULL;

UPDATE calendar_events SET color = '#4CAF50' WHERE color IS NULL;
UPDATE calendar_events SET repetitive = FALSE WHERE repetitive IS NULL;
UPDATE calendar_events SET tactile_reminder = FALSE WHERE tactile_reminder IS NULL;

-- Etapa 3: Alterar colunas para NOT NULL e definir DEFAULT

ALTER TABLE users
  MODIFY COLUMN theme ENUM('LIGHT', 'DARK', 'HIGH_CONTRAST') NOT NULL DEFAULT 'LIGHT',
  MODIFY COLUMN default_reminder_minutes INT NOT NULL DEFAULT 15,
  MODIFY COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE categories
  MODIFY COLUMN color VARCHAR(7) NOT NULL DEFAULT '#FF9F89',
  MODIFY COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE goals
  MODIFY COLUMN creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tasks
  MODIFY COLUMN priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
  MODIFY COLUMN status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'NOT_STARTED',
  MODIFY COLUMN creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN focus_mode BOOLEAN NOT NULL DEFAULT FALSE,
  MODIFY COLUMN is_done BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE subtasks
  MODIFY COLUMN is_done BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE calendar_events
  MODIFY COLUMN color VARCHAR(7) NOT NULL DEFAULT '#4CAF50',
  MODIFY COLUMN repetitive BOOLEAN NOT NULL DEFAULT FALSE,
  MODIFY COLUMN tactile_reminder BOOLEAN NOT NULL DEFAULT FALSE;
