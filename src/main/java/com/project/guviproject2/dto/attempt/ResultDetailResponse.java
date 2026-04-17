package com.project.guviproject2.dto.attempt;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDetailResponse {
    private Long resultId;
    private Long quizId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;
    private Map<Long, Long> answers;
}
