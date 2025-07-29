package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamAnswerRequestDto;
import fu.sep.apjf.dto.request.ExamStatusDto;
import fu.sep.apjf.dto.request.SubmitExamDto;
import fu.sep.apjf.dto.response.ExamHistoryDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.ExamHistoryMapper;
import fu.sep.apjf.mapper.ExamResultMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamResultDetailRepository examResultDetailRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public ExamResultResponseDto startExam(String examId, Long userId) {
        // Kiểm tra exam có tồn tại không
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));

        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));

        // Kiểm tra xem có bài thi đang làm dở không
        Optional<ExamResult> inProgressExam = examResultRepository.findByUserAndExamAndSubmittedAtIsNull(user, exam);
        if (inProgressExam.isPresent()) {
            throw new RuntimeException("Người dùng đang làm bài thi này");
        }

        // Kiểm tra xem đã có kết quả hoàn thành chưa - nếu có thì xóa để cho phép thi lại
        Optional<ExamResult> existingResult = examResultRepository.findByUserAndExam(user, exam);
        if (existingResult.isPresent() && existingResult.get().getSubmittedAt() != null) {
            // Xóa kết quả cũ để cho phép thi lại
            ExamResult oldResult = existingResult.get();
            // Xóa các chi tiết trước
            if (!oldResult.getDetails().isEmpty()) {
                examResultDetailRepository.deleteAll(oldResult.getDetails());
            }
            // Xóa kết quả chính
            examResultRepository.delete(oldResult);
        }

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

    // Đơn giản hóa method submitExam - loại bỏ isAutoSubmit
    public ExamResultResponseDto submitExam(SubmitExamDto submitExamDto, Long userId) {
        // Tìm bài thi dựa vào examId
        String examId = submitExamDto.examId();
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));

        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));

        // Tìm bài làm đang dở
        ExamResult examResult = examResultRepository.findByUserAndExamAndSubmittedAtIsNull(user, exam)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài thi đang làm dở"));

        if (examResult.getSubmittedAt() != null) {
            throw new RuntimeException("Bài thi đã được nộp rồi");
        }

        // Xóa các câu trả lời cũ và lưu câu trả lời mới
        if (!examResult.getDetails().isEmpty()) {
            examResultDetailRepository.deleteAll(examResult.getDetails());
            examResult.getDetails().clear();
        }

        int correctAnswers = 0;
        int totalQuestionsInExam = exam.getQuestions().size();

        // Lưu các câu trả lời từ request
        for (ExamAnswerRequestDto answerDto : submitExamDto.answers()) {
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
                Option selectedOption = optionRepository.findById(answerDto.selectedOptionId())
                        .orElse(null);
                answer.setSelectedOption(selectedOption);
            }

            examResultDetailRepository.save(answer);
            examResult.getDetails().add(answer);
        }

        // Tính điểm dựa trên tổng số câu hỏi trong đề thi
        float score = totalQuestionsInExam > 0 ? Math.round(((float) correctAnswers / totalQuestionsInExam) * 100) : 0;
        EnumClass.ExamStatus status = score >= 50.0 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED;

        examResult.setSubmittedAt(LocalDateTime.now());
        examResult.setScore(score);
        examResult.setStatus(status);

        ExamResult savedResult = examResultRepository.save(examResult);
        return ExamResultMapper.toDto(savedResult);
    }

    private boolean checkAnswer(Question question, ExamAnswerRequestDto answerDto) {
        return switch (question.getType()) {
            case MULTIPLE_CHOICE, TRUE_FALSE -> {
                if (answerDto.selectedOptionId() != null) {
                    Option selectedOption = optionRepository.findById(answerDto.selectedOptionId())
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

    public ExamResultResponseDto getExamResultById(String id) {
        ExamResult result = examResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả bài thi: " + id));
        return ExamResultMapper.toDto(result);
    }

    public List<ExamResultResponseDto> getExamResultsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUser(user);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultResponseDto> getExamResultsByExamId(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        List<ExamResult> results = examResultRepository.findByExam(exam);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultResponseDto> getInProgressExams() {
        List<ExamResult> results = examResultRepository.findBySubmittedAtIsNull();
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultResponseDto> getPassedExamsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUserAndStatus(user, EnumClass.ExamStatus.PASSED);
        return ExamResultMapper.toDtoList(results);
    }

    public List<ExamResultResponseDto> getFailedExamsByUser(Long userId) {
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
                .orElseThrow(() -> new RuntimeException("Không tìm th��y người dùng: " + userId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        return examResultRepository.existsByUserAndExam(user, exam);
    }

    public List<ExamHistoryDto> getExamHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUserAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(user);
        return ExamHistoryMapper.toDtoList(results);
    }

    public Page<ExamHistoryDto> getExamHistory(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamResult> results = examResultRepository.findByUserAndSubmittedAtIsNotNull(user, pageable);
        return results.map(ExamHistoryMapper::toDto);
    }

    public List<ExamHistoryDto> getExamHistoryByStatus(Long userId, EnumClass.ExamStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        List<ExamResult> results = examResultRepository.findByUserAndStatusOrderBySubmittedAtDesc(user, status);
        return ExamHistoryMapper.toDtoList(results);
    }

    public List<ExamHistoryDto> getRecentExamHistory(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        Pageable pageable = PageRequest.of(0, limit);
        Page<ExamResult> results = examResultRepository.findByUserAndSubmittedAtIsNotNull(user, pageable);
        return ExamHistoryMapper.toDtoList(results.getContent());
    }


    // Method mới: Submit bài thi với examId
    public ExamResultResponseDto submitExam(String examId, SubmitExamDto submitExamDto, Long userId) {
        return this.submitExam(submitExamDto, userId);
    }

    // Method mới: Lấy kết quả bài thi của user cho exam cụ thể
    public ExamResultResponseDto getExamResult(String examId, Long userId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));

        ExamResult examResult = examResultRepository.findByUserAndExam(user, exam)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa làm bài thi này"));

        return ExamResultMapper.toDto(examResult);
    }

    // Method mới: Kiểm tra trạng thái bài thi của user
    public ExamStatusDto getExamStatus(String examId, Long userId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));

        Optional<ExamResult> examResultOpt = examResultRepository.findByUserAndExam(user, exam);

        if (examResultOpt.isEmpty()) {
            // Chưa bắt đầu làm bài
            return ExamStatusDto.notStarted();
        }

        ExamResult examResult = examResultOpt.get();

        if (examResult.getSubmittedAt() == null) {
            // Đang làm bài
            return ExamStatusDto.inProgress(examResult.getId(), examResult.getStartedAt());
        } else {
            // Đã hoàn thành
            return ExamStatusDto.completed(
                    examResult.getId(),
                    examResult.getStartedAt(),
                    examResult.getSubmittedAt(),
                    examResult.getStatus(),
                    examResult.getScore()
            );
        }
    }
}
