-- Criar tabela de listas de tarefas
CREATE TABLE lists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    color VARCHAR(7),
    icon VARCHAR(10),
    type VARCHAR(50) NOT NULL,
    repetition_interval_amount INT,
    repetition_interval_type VARCHAR(20),
    repeat_when_complete BOOLEAN DEFAULT FALSE,
    manual_restart_allowed BOOLEAN DEFAULT FALSE,
    deadline TIMESTAMP,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,

    CONSTRAINT fk_lists_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_lists_user_id (user_id),
    INDEX idx_lists_type (type),
    INDEX idx_lists_creation_date (creation_date)
);

-- Adicionar coluna list_id na tabela tasks (permitindo NULL para ON DELETE SET NULL)
ALTER TABLE tasks
ADD COLUMN list_id INT NULL,
ADD CONSTRAINT fk_tasks_list_id FOREIGN KEY (list_id) REFERENCES lists(id) ON DELETE SET NULL;

-- Criar índice para melhorar performance das consultas
CREATE INDEX idx_tasks_list_id ON tasks(list_id);
