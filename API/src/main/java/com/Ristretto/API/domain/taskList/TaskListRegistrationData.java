package com.Ristretto.API.domain.taskList;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskListRegistrationData(
        @NotBlank(message = "{title.required}")
        String title,
        String color,
        String icon,

        @NotNull(message = "{type.required}")
        ListType type,
        Integer repetitionIntervalAmount,
        IntervalType repetitionIntervalType,
        Boolean repeatWhenComplete,
        Boolean manualRestartAllowed,
        LocalDateTime deadline,
        @NotNull(message = "{user.required}")
        Long userId
){
        @AssertTrue(message = "Repetition type is required for cyclic lists")
        @JsonIgnore
        public boolean isRepetitionValid() {
                if (type == ListType.CYCLIC) {
                        return repetitionIntervalType != null && repetitionIntervalAmount != null;
                }
                return true;
        }
        @AssertTrue(message = "Deadline is required for standard lists")
        @JsonIgnore
        public boolean isDeadlineValid() {
                if (type == ListType.STANDARD) {
                        return deadline != null;
                }
                return true;
        }
}
