package com.project.guviproject2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.attempt.ResultDetailResponse;
import com.project.guviproject2.dto.attempt.ResultSummaryResponse;
import com.project.guviproject2.service.AttemptService;

@RestController
@RequestMapping("/api/admin/results")
public class AdminResultController {

    private final AttemptService attemptService;

    public AdminResultController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping
    public ResponseEntity<List<ResultSummaryResponse>> all() {
        return ResponseEntity.ok(attemptService.listAllResults());
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ResultDetailResponse> one(@PathVariable Long resultId, Principal principal) {
        return ResponseEntity.ok(attemptService.getResult(resultId, principal.getName()));
    }
}
