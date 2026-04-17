package com.project.guviproject2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.guviproject2.dto.quiz.OptionCreateRequest;
import com.project.guviproject2.dto.quiz.OptionViewResponse;
import com.project.guviproject2.dto.quiz.AdminOptionViewResponse;
import com.project.guviproject2.dto.quiz.AdminQuestionViewResponse;
import com.project.guviproject2.dto.quiz.AdminQuizDetailResponse;
import com.project.guviproject2.dto.quiz.QuestionCreateRequest;
import com.project.guviproject2.dto.quiz.QuestionViewResponse;
import com.project.guviproject2.dto.quiz.QuizCreateRequest;
import com.project.guviproject2.dto.quiz.QuizDetailResponse;
import com.project.guviproject2.dto.quiz.QuizSummaryResponse;
import com.project.guviproject2.entity.Question;
import com.project.guviproject2.entity.Quiz;
import com.project.guviproject2.entity.QuizOption;
import com.project.guviproject2.exception.BadRequestException;
import com.project.guviproject2.exception.ResourceNotFoundException;
import com.project.guviproject2.repository.QuizRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizSummaryResponse createQuiz(QuizCreateRequest request, String createdBy) {
        validateQuestions(request.getQuestions());
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setCompany(request.getCompany());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setCreatedBy(createdBy);
        if (request.getQuestions() != null) {
            mapQuestions(request.getQuestions(), quiz);
        }
        Quiz saved = quizRepository.save(quiz);
        return toSummary(saved);
    }

    @Transactional
    public QuizSummaryResponse updateQuiz(Long quizId, QuizCreateRequest request) {
        validateQuestions(request.getQuestions());
        Quiz quiz = findQuiz(quizId);
        quiz.setTitle(request.getTitle());
        quiz.setCompany(request.getCompany());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        if (request.getQuestions() != null) {
            quiz.getQuestions().clear();
            mapQuestions(request.getQuestions(), quiz);
        }
        Quiz saved = quizRepository.save(quiz);
        return toSummary(saved);
    }

    @Transactional
    public AdminQuizDetailResponse addQuestion(Long quizId, QuestionCreateRequest request) {
        validateQuestion(request);
        Quiz quiz = findQuiz(quizId);
        Question question = new Question();
        question.setText(request.getText());
        question.setQuiz(quiz);
        for (OptionCreateRequest optionRequest : request.getOptions()) {
            QuizOption option = new QuizOption();
            option.setText(optionRequest.getText());
            option.setCorrect(optionRequest.isCorrect());
            option.setQuestion(question);
            question.getOptions().add(option);
        }
        quiz.getQuestions().add(question);
        Quiz saved = quizRepository.save(quiz);
        return toAdminDetail(saved);
    }

    @Transactional
    public AdminQuizDetailResponse updateQuestion(Long quizId, Long questionId, QuestionCreateRequest request) {
        validateQuestion(request);
        Quiz quiz = findQuiz(quizId);
        Question question = quiz.getQuestions().stream()
                .filter(value -> value.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        question.setText(request.getText());
        question.getOptions().clear();
        for (OptionCreateRequest optionRequest : request.getOptions()) {
            QuizOption option = new QuizOption();
            option.setText(optionRequest.getText());
            option.setCorrect(optionRequest.isCorrect());
            option.setQuestion(question);
            question.getOptions().add(option);
        }
        Quiz saved = quizRepository.save(quiz);
        return toAdminDetail(saved);
    }

    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        Quiz quiz = findQuiz(quizId);
        boolean removed = quiz.getQuestions().removeIf(question -> question.getId().equals(questionId));
        if (!removed) {
            throw new ResourceNotFoundException("Question not found");
        }
        quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found");
        }
        quizRepository.deleteById(quizId);
    }

    public List<QuizSummaryResponse> listQuizzes() {
        return quizRepository.findAll().stream().map(this::toSummary).toList();
    }

    public QuizDetailResponse getQuizForAttempt(Long id) {
        Quiz quiz = findQuiz(id);
        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getCompany(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getQuestions().stream().map(question -> new QuestionViewResponse(
                        question.getId(),
                        question.getText(),
                        question.getOptions().stream()
                                .map(option -> new OptionViewResponse(option.getId(), option.getText()))
                                .toList()))
                        .toList());
    }

    public AdminQuizDetailResponse getQuizForAdmin(Long id) {
        return toAdminDetail(findQuiz(id));
    }

    public Quiz getQuizEntity(Long id) {
        return findQuiz(id);
    }

    private Quiz findQuiz(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    private void validateQuestions(List<QuestionCreateRequest> questions) {
        if (questions == null) {
            return;
        }
        for (QuestionCreateRequest question : questions) {
            validateQuestion(question);
        }
    }

    private void validateQuestion(QuestionCreateRequest question) {
        if (question.getOptions().size() < 2) {
            throw new BadRequestException("Each question must have at least two options");
        }
        long correct = question.getOptions().stream().filter(OptionCreateRequest::isCorrect).count();
        if (correct != 1) {
            throw new BadRequestException("Each question must have exactly one correct option");
        }
    }

    private void mapQuestions(List<QuestionCreateRequest> questionRequests, Quiz quiz) {
        for (QuestionCreateRequest questionRequest : questionRequests) {
            Question question = new Question();
            question.setText(questionRequest.getText());
            question.setQuiz(quiz);
            for (OptionCreateRequest optionRequest : questionRequest.getOptions()) {
                QuizOption option = new QuizOption();
                option.setText(optionRequest.getText());
                option.setCorrect(optionRequest.isCorrect());
                option.setQuestion(question);
                question.getOptions().add(option);
            }
            quiz.getQuestions().add(question);
        }
    }

    private QuizSummaryResponse toSummary(Quiz quiz) {
        return new QuizSummaryResponse(
                quiz.getId(),
                quiz.getCompany(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getQuestions().size(),
                quiz.getCreatedAt());
    }

    private AdminQuizDetailResponse toAdminDetail(Quiz quiz) {
        return new AdminQuizDetailResponse(
                quiz.getId(),
                quiz.getCompany(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getQuestions().stream().map(question -> new AdminQuestionViewResponse(
                        question.getId(),
                        question.getText(),
                        question.getOptions().stream()
                                .map(option -> new AdminOptionViewResponse(option.getId(), option.getText(), option.isCorrect()))
                                .toList()))
                        .toList());
    }
}
