-- Garantir que o banco está sendo usado
USE tcc_tdah_organizer;

ALTER TABLE subtasks
ADD COLUMN due_date DATETIME;
