package com.project.guviproject2.dto.quiz;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminQuizDetailResponse {
    private Long id;
    private String company;
    private String title;
    private String description;
    private Integer durationMinutes;
    private List<AdminQuestionViewResponse> questions;
}
