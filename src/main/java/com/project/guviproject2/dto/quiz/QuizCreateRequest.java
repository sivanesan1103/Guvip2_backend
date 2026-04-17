package com.project.guviproject2.dto.quiz;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotBlank
    private String description;

    @Min(1)
    private Integer durationMinutes;

    @Valid
    private List<QuestionCreateRequest> questions;
}
