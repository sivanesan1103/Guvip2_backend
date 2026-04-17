package com.project.guviproject2.dto.quiz;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizSummaryResponse {
    private Long id;
    private String company;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer questionCount;
    private LocalDateTime createdAt;
}
