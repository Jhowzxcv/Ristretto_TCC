package com.Ristretto.API.domain.taskList;

import java.time.LocalDateTime;

/**
 * DTO para listagem de listas de tarefas.
 * Retorna apenas os campos principais, sem expor todos os detalhes da entidade.
 */
public record TaskListDataListing(
        Long id,
        String title,
        String color,
        String icon,
        ListType type,
        LocalDateTime deadline
) {
    // Construtor auxiliar para converter diretamente da entidade
    public TaskListDataListing(TaskList taskList) {
        this(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getColor(),
                taskList.getIcon(),
                taskList.getType(),
                taskList.getDeadline()
        );
    }
}