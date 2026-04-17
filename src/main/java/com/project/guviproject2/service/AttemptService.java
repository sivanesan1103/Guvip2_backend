package com.project.guviproject2.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.guviproject2.dto.attempt.AttemptResultResponse;
import com.project.guviproject2.dto.attempt.ResultDetailResponse;
import com.project.guviproject2.dto.attempt.ResultSummaryResponse;
import com.project.guviproject2.dto.attempt.SubmitAttemptRequest;
import com.project.guviproject2.entity.Question;
import com.project.guviproject2.entity.Quiz;
import com.project.guviproject2.entity.QuizOption;
import com.project.guviproject2.entity.Result;
import com.project.guviproject2.entity.Role;
import com.project.guviproject2.entity.User;
import com.project.guviproject2.exception.ResourceNotFoundException;
import com.project.guviproject2.exception.BadRequestException;
import com.project.guviproject2.repository.QuizRepository;
import com.project.guviproject2.repository.ResultRepository;
import com.project.guviproject2.repository.UserRepository;

@Service
public class AttemptService {

    private final QuizService quizService;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final QuizRepository quizRepository;
    private final EmailService emailService;

    public AttemptService(QuizService quizService,
                          UserRepository userRepository,
                          ResultRepository resultRepository,
                          QuizRepository quizRepository,
                          EmailService emailService) {
        this.quizService = quizService;
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
        this.quizRepository = quizRepository;
        this.emailService = emailService;
    }

    @Transactional
    public AttemptResultResponse submit(Long quizId, SubmitAttemptRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!Role.PARTICIPANT.equals(user.getRole())) {
            throw new AccessDeniedException("Only participants can take quizzes");
        }
        Quiz quiz = quizService.getQuizEntity(quizId);
        int correctCount = 0;
        int total = quiz.getQuestions().size();
        Map<Long, Question> questionById = quiz.getQuestions().stream()
                .collect(LinkedHashMap::new, (map, question) -> map.put(question.getId(), question), Map::putAll);

        for (Map.Entry<Long, Long> entry : request.getAnswers().entrySet()) {
            Question question = questionById.get(entry.getKey());
            if (question == null) {
                throw new BadRequestException("Submitted answers contain invalid question ID " + entry.getKey());
            }
            boolean optionBelongsToQuestion = question.getOptions().stream()
                    .anyMatch(option -> option.getId().equals(entry.getValue()));
            if (!optionBelongsToQuestion) {
                throw new BadRequestException("Selected option " + entry.getValue() + " does not belong to question " + entry.getKey());
            }
        }

        Map<Long, Long> normalizedAnswers = new LinkedHashMap<>();

        for (Question question : quiz.getQuestions()) {
            Long selectedOptionId = request.getAnswers().get(question.getId());
            if (selectedOptionId != null) {
                normalizedAnswers.put(question.getId(), selectedOptionId);
            }
            QuizOption correctOption = question.getOptions().stream().filter(QuizOption::isCorrect).findFirst().orElse(null);
            if (correctOption != null && correctOption.getId().equals(selectedOptionId)) {
                correctCount++;
            }
        }

        Result result = new Result();
        result.setUserId(user.getId());
        result.setQuizId(quiz.getId());
        result.setAnswersJson(serializeAnswers(normalizedAnswers));
        result.setScore(correctCount);
        result.setTotalQuestions(total);
        Result saved = resultRepository.save(result);

        emailService.sendResultEmail(user.getEmail(), quiz.getTitle(), correctCount, total);

        return new AttemptResultResponse(
                saved.getId(),
                saved.getQuizId(),
                saved.getScore(),
                saved.getTotalQuestions(),
                saved.getScore(),
                saved.getTotalQuestions() - saved.getScore(),
                saved.getSubmittedAt());
    }

    public List<ResultSummaryResponse> listMyResults(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<Long, Quiz> quizById = loadQuizLookup();
        return resultRepository.findByUserIdOrderBySubmittedAtDesc(user.getId()).stream()
                .map(result -> toSummary(result, user.getEmail(), quizById))
                .toList();
    }

    public List<ResultSummaryResponse> listResultsByUserId(Long userId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!Role.ADMIN.equals(requester.getRole()) && !requester.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to view this user's results");
        }
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<Long, Quiz> quizById = loadQuizLookup();
        return resultRepository.findByUserIdOrderBySubmittedAtDesc(targetUser.getId()).stream()
                .map(result -> toSummary(result, targetUser.getEmail(), quizById))
                .toList();
    }

    public List<ResultSummaryResponse> listAllResults() {
        Map<Long, User> userById = loadUserLookup();
        Map<Long, Quiz> quizById = loadQuizLookup();
        return resultRepository.findAllByOrderBySubmittedAtDesc().stream()
                .map(result -> {
                    User user = userById.get(result.getUserId());
                    String userEmail = user != null ? user.getEmail() : "-";
                    return toSummary(result, userEmail, quizById);
                })
                .toList();
    }

    public ResultDetailResponse getResult(Long resultId, String email) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!Role.ADMIN.equals(user.getRole()) && !result.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Not allowed to view this result");
        }
        return new ResultDetailResponse(
                result.getId(),
                result.getQuizId(),
                result.getScore(),
                result.getTotalQuestions(),
                result.getSubmittedAt(),
                deserializeAnswers(result.getAnswersJson()));
    }

    private String serializeAnswers(Map<Long, Long> answers) {
        if (answers.isEmpty()) {
            return "";
        }
        return answers.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }

    private Map<Long, Long> deserializeAnswers(String payload) {
        if (payload == null || payload.isBlank()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> parsed = new LinkedHashMap<>();
        Arrays.stream(payload.split(","))
                .forEach(pair -> {
                    String[] tokens = pair.split(":");
                    if (tokens.length == 2) {
                        parsed.put(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]));
                    }
                });
        return parsed;
    }

    private ResultSummaryResponse toSummary(Result result, String email, Map<Long, Quiz> quizById) {
        Quiz quiz = quizById.get(result.getQuizId());
        String quizTitle = quiz != null ? quiz.getTitle() : "-";
        String company = quiz != null ? quiz.getCompany() : "-";
        return new ResultSummaryResponse(
                result.getId(),
                result.getQuizId(),
                quizTitle,
                company,
                email,
                result.getScore(),
                result.getTotalQuestions(),
                result.getSubmittedAt());
    }

    private Map<Long, Quiz> loadQuizLookup() {
        Map<Long, Quiz> quizById = new HashMap<>();
        for (Quiz quiz : quizRepository.findAll()) {
            quizById.put(quiz.getId(), quiz);
        }
        return quizById;
    }

    private Map<Long, User> loadUserLookup() {
        Map<Long, User> userById = new HashMap<>();
        for (User user : userRepository.findAll()) {
            userById.put(user.getId(), user);
        }
        return userById;
    }
}
