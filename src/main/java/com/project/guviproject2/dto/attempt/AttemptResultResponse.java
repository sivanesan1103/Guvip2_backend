package com.project.guviproject2.dto.attempt;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttemptResultResponse {
    private Long resultId;
    private Long quizId;
    private Integer score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private LocalDateTime submittedAt;
}
