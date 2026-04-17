package com.project.guviproject2.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OptionCreateRequest {
    @NotBlank
    private String text;

    private boolean correct;
}
