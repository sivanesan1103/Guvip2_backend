package com.project.guviproject2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.guviproject2.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserIdOrderBySubmittedAtDesc(Long userId);

    List<Result> findAllByOrderBySubmittedAtDesc();
}
