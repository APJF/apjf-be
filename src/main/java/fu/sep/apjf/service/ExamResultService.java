package fu.sep.apjf.service;

import fu.sep.apjf.dto.AnswerSubmissionDto;
import fu.sep.apjf.dto.ExamResultDto;
import fu.sep.apjf.dto.StartExamDto;
import fu.sep.apjf.dto.SubmitExamDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.ExamResultMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamResultDetailRepository examResultDetailRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserRepository userRepository;

    public ExamResultDto startExam(StartExamDto startExamDto) {
        // Kiểm tra exam có tồn tại không
        Exam exam = examRepository.findById(startExamDto.examId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + startExamDto.examId()));

        // Tìm user
        Long userId = Long.parseLong(startExamDto.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + startExamDto.userId()));

        // Kiểm tra user đã làm bài này chưa - sử dụng entity objects
        examResultRepository.findByUserAndExam(user, exam)
                .ifPresent(existingResult -> {
                    if (existingResult.getSubmittedAt() == null) {
                        throw new RuntimeException("Người dùng đang làm bài thi này");
                    }
                    throw new RuntimeException("Người dùng đã làm bài thi này rồi");
                });

        ExamResult examResult = ExamResult.builder()
                .id(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now())
                .user(user)
                .exam(exam)
                .status(EnumClass.ExamStatus.IN_PROGRESS)
                .build();

        ExamResult savedResult = examResultRepository.save(examResult);

        // Sử dụng mapper thay vì phương thức nội bộ
        return ExamResultMapper.toDto(savedResult);
    }

    // Cập nhật phương thức submitExam để nhận thêm userId
    public ExamResultDto submitExam(SubmitExamDto submitExamDto, String userId) {
        // Tìm bài thi dựa vào examId
        String examId = submitExamDto.examId();
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));

        // Tìm user
        Long userIdLong = Long.parseLong(userId);
        User user = userRepository.findById(userIdLong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));

        // Tìm bài làm đang dở
        ExamResult examResult = examResultRepository.findByUserAndExamAndSubmittedAtIsNull(user, exam)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài thi đang làm dở"));

        if (examResult.getSubmittedAt() != null) {
            throw new RuntimeException("Bài thi đã được nộp rồi");
        }

        // Xóa các câu trả lời cũ nếu có (để tránh trùng lặp)
        if (!examResult.getDetails().isEmpty()) {
            examResultDetailRepository.deleteAll(examResult.getDetails());
            examResult.getDetails().clear();
        }

        // Lưu các câu trả lời mới
        int correctAnswers = 0;
        int totalQuestions = submitExamDto.answers().size();

        for (AnswerSubmissionDto answerDto : submitExamDto.answers()) {
            Question question = questionRepository.findById(answerDto.questionId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi: " + answerDto.questionId()));

            boolean isCorrect = checkAnswer(question, answerDto);
            if (isCorrect) correctAnswers++;

            ExamResultDetail answer = ExamResultDetail.builder()
                    .id(UUID.randomUUID().toString())
                    .userAnswer(answerDto.userAnswer())
                    .isCorrect(isCorrect)
                    .examResult(examResult)
                    .question(question)
                    .build();

            if (answerDto.selectedOptionId() != null) {
                QuestionOption selectedOption = questionOptionRepository.findById(answerDto.selectedOptionId())
                        .orElse(null);
                answer.setSelectedOption(selectedOption);
            }

            examResultDetailRepository.save(answer);
            examResult.getDetails().add(answer);
        }

        float score = totalQuestions > 0 ? (float) correctAnswers / totalQuestions * 10 : 0;
        EnumClass.ExamStatus status = score >= 5.0 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED;

        examResult.setSubmittedAt(LocalDateTime.now());
        examResult.setScore(score);
        examResult.setStatus(status);

        ExamResult savedResult = examResultRepository.save(examResult);

        return ExamResultMapper.toDto(savedResult);
    }

    private boolean checkAnswer(Question question, AnswerSubmissionDto answerDto) {
        return switch (question.getType()) {
            case MULTIPLE_CHOICE, TRUE_FALSE -> {
                if (answerDto.selectedOptionId() != null) {
                    QuestionOption selectedOption = questionOptionRepository.findById(answerDto.selectedOptionId())
                            .orElse(null);
                    yield selectedOption != null && selectedOption.getIsCorrect();
                }
                yield false;
            }
            case WRITING -> question.getCorrectAnswer() != null &&
                    question.getCorrectAnswer().equalsIgnoreCase(answerDto.userAnswer());
            default -> false;
        };
    }

    public ExamResultDto getExamResultById(String id) {
        ExamResult result = examResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả bài thi: " + id));
        return ExamResultMapper.toDto(result);
    }

    public List<ExamResultDto> getExamResultsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUser(user);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultDto> getExamResultsByExamId(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        List<ExamResult> results = examResultRepository.findByExam(exam);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultDto> getInProgressExams() {
        List<ExamResult> results = examResultRepository.findBySubmittedAtIsNull();
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultDto> getPassedExamsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUserAndStatus(user, EnumClass.ExamStatus.PASSED);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultDto> getFailedExamsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUserAndStatus(user, EnumClass.ExamStatus.FAILED);
        return ExamResultMapper.toDtoList(results);
    }

    public Double getAverageScoreByExamId(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        return examResultRepository.getAverageScoreByExam(exam);
    }

    public long countCompletedExamsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        return examResultRepository.countByUserAndStatus(user, EnumClass.ExamStatus.PASSED) +
                examResultRepository.countByUserAndStatus(user, EnumClass.ExamStatus.FAILED);
    }

    public boolean hasUserTakenExam(Long userId, String examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        return examResultRepository.existsByUserAndExam(user, exam);
    }
}
