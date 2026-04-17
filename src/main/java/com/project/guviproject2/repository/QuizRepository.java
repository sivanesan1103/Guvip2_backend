package com.project.guviproject2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.guviproject2.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
