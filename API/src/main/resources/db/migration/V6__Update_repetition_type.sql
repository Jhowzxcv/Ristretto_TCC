-- Atualiza os valores existentes para maiúsculo
UPDATE calendar_events
SET repetition_type = UPPER(repetition_type)
WHERE repetition_type IS NOT NULL;

-- Altera o ENUM para valores em maiúsculo
ALTER TABLE calendar_events
MODIFY COLUMN repetition_type ENUM('DAILY', 'WEEKLY', 'MONTHLY');
