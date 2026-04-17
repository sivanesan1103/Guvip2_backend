package com.project.guviproject2.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOptionViewResponse {
    private Long id;
    private String text;
    private boolean correct;
}
