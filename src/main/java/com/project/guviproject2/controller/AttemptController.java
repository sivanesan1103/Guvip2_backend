package com.project.guviproject2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.attempt.AttemptResultResponse;
import com.project.guviproject2.dto.attempt.ResultDetailResponse;
import com.project.guviproject2.dto.attempt.ResultSummaryResponse;
import com.project.guviproject2.dto.attempt.SubmitAttemptRequest;
import com.project.guviproject2.service.AttemptService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<AttemptResultResponse> submit(@PathVariable Long quizId,
                                                        @Valid @RequestBody SubmitAttemptRequest request,
                                                        Principal principal) {
        return ResponseEntity.ok(attemptService.submit(quizId, request, principal.getName()));
    }

    @GetMapping("/my-results")
    public ResponseEntity<List<ResultSummaryResponse>> myResults(Principal principal) {
        return ResponseEntity.ok(attemptService.listMyResults(principal.getName()));
    }

    @GetMapping("/users/{userId}/results")
    public ResponseEntity<List<ResultSummaryResponse>> userResults(@PathVariable Long userId, Principal principal) {
        return ResponseEntity.ok(attemptService.listResultsByUserId(userId, principal.getName()));
    }

    @GetMapping("/results/{resultId}")
    public ResponseEntity<ResultDetailResponse> getResult(@PathVariable Long resultId, Principal principal) {
        return ResponseEntity.ok(attemptService.getResult(resultId, principal.getName()));
    }
}
