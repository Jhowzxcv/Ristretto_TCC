package com.Ristretto.API.domain.taskList;

import com.Ristretto.API.domain.task.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record TaskListDataDetails(
        Long id,
        String title,
        String color,
        String icon,
        ListType type,
        Integer repetitionIntervalAmount,
        IntervalType repetitionIntervalType,
        Boolean repeatWhenComplete,
        Boolean manualRestartAllowed,
        LocalDateTime deadline,
        LocalDateTime creationDate,
        Long userId
        //,List<Long> taskIds
) {
    public TaskListDataDetails(TaskList taskList) {
        this(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getColor(),
                taskList.getIcon(),
                taskList.getType(),
                taskList.getRepetitionIntervalAmount(),
                taskList.getRepetitionIntervalType(),
                taskList.getRepeatWhenComplete(),
                taskList.getManualRestartAllowed(),
                taskList.getDeadline(),
                taskList.getCreationDate(),
                taskList.getUser() != null ? taskList.getUser().getId() : null
                //,extractTaskIds(taskList)
        );
    }

//    private static List<Long> extractTaskIds(TaskList taskList) {
//        return taskList.getTasks() == null
//                ? List.of()
//                : taskList.getTasks().stream()
//                .map(Task::getId)
//                .collect(Collectors.toList());
//    }
}
