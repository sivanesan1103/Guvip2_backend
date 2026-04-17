package com.project.guviproject2.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.quiz.AdminQuizDetailResponse;
import com.project.guviproject2.dto.quiz.QuestionCreateRequest;
import com.project.guviproject2.dto.quiz.QuizCreateRequest;
import com.project.guviproject2.dto.quiz.QuizSummaryResponse;
import com.project.guviproject2.service.QuizService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    private final QuizService quizService;

    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizSummaryResponse> create(@Valid @RequestBody QuizCreateRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request, principal.getName()));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<AdminQuizDetailResponse> getOne(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizForAdmin(quizId));
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizSummaryResponse> update(@PathVariable Long quizId, @Valid @RequestBody QuizCreateRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, request));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> delete(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<AdminQuizDetailResponse> addQuestion(@PathVariable Long quizId, @Valid @RequestBody QuestionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.addQuestion(quizId, request));
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<AdminQuizDetailResponse> updateQuestion(@PathVariable Long quizId,
                                                                  @PathVariable Long questionId,
                                                                  @Valid @RequestBody QuestionCreateRequest request) {
        return ResponseEntity.ok(quizService.updateQuestion(quizId, questionId, request));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        quizService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }
}
