-- V8__rename_lists_to_task_list.sql
-- Migration para renomear tabela 'lists' para 'task_list' e ajustar todas as referências

-- Garantir que estamos no banco correto
USE tcc_tdah_organizer;

-- Passo 1: Remover a foreign key constraint existente
ALTER TABLE tasks
DROP FOREIGN KEY fk_tasks_list_id;

-- Passo 2: Remover o índice da tabela tasks
DROP INDEX idx_tasks_list_id ON tasks;

-- Passo 3: Renomear a tabela lists para task_list
RENAME TABLE lists TO task_list;

-- Passo 4: Renomear a coluna list_id para task_list_id na tabela tasks
ALTER TABLE tasks
CHANGE COLUMN list_id task_list_id INT NULL;

-- Passo 5: Recriar a foreign key constraint com novo nome
ALTER TABLE tasks
ADD CONSTRAINT fk_tasks_task_list_id FOREIGN KEY (task_list_id) REFERENCES task_list(id) ON DELETE SET NULL;

-- Passo 6: Recriar o índice com novo nome para otimização
CREATE INDEX idx_tasks_task_list_id ON tasks(task_list_id);