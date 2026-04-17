package com.project.guviproject2.dto.attempt;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAttemptRequest {
    @NotNull
    private Map<Long, Long> answers;
}
