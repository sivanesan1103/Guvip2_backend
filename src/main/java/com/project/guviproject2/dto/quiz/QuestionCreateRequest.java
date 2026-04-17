package com.project.guviproject2.dto.quiz;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuestionCreateRequest {
    @NotBlank
    private String text;

    @Valid
    @NotEmpty
    private List<OptionCreateRequest> options;
}
