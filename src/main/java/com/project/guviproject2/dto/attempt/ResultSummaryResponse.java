package com.project.guviproject2.dto.attempt;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultSummaryResponse {
    private Long resultId;
    private Long quizId;
    private String quizTitle;
    private String company;
    private String userEmail;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;
}
