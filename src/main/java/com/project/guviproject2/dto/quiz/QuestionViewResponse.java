package com.project.guviproject2.dto.quiz;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionViewResponse {
    private Long id;
    private String text;
    private List<OptionViewResponse> options;
}
