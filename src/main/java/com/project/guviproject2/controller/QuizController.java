package com.project.guviproject2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.quiz.QuizDetailResponse;
import com.project.guviproject2.dto.quiz.QuizSummaryResponse;
import com.project.guviproject2.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuizSummaryResponse>> getAll() {
        return ResponseEntity.ok(quizService.listQuizzes());
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailResponse> getOne(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizForAttempt(quizId));
    }
}
