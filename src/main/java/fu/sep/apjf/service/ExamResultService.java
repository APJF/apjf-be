package fu.sep.apjf.service;

import fu.sep.apjf.dto.AnswerSubmissionDto;
import fu.sep.apjf.dto.ExamHistoryDto;
import fu.sep.apjf.dto.ExamResultDto;
import fu.sep.apjf.dto.StartExamDto;
import fu.sep.apjf.dto.SubmitExamDto;
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

    // Cập nhật phương thức submitExam để nhận thêm userId
    public ExamResultDto submitExam(SubmitExamDto submitExamDto, String userId, boolean isAutoSubmit) {
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

        // Kiểm tra thời gian làm bài
        LocalDateTime startedAt = examResult.getStartedAt();
        Double duration = examResult.getExam().getDuration();
        LocalDateTime expectedEndTime = startedAt.plusMinutes(duration.longValue());
        boolean isTimeExpired = LocalDateTime.now().isAfter(expectedEndTime);

        // Nếu không phải auto submit và đã hết giờ
        if (!isAutoSubmit && isTimeExpired) {
            throw new RuntimeException("Đã hết thời gian làm bài");
        }

        // Xóa các câu trả lời cũ nếu có và người dùng đang submit mới
        if (!isAutoSubmit && !examResult.getDetails().isEmpty()) {
            examResultDetailRepository.deleteAll(examResult.getDetails());
            examResult.getDetails().clear();
        }

        int correctAnswers = 0;
        int totalQuestionsInExam = exam.getQuestions().size(); // Lấy tổng số câu hỏi thực tế trong đề thi

        if (!isAutoSubmit) {
            // Trường hợp submit thông thường: lưu các câu trả lời mới
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
        } else {
            // Trường hợp auto submit: tính điểm dựa trên câu trả lời đã có
            for (ExamResultDetail detail : examResult.getDetails()) {
                if (detail.getIsCorrect()) {
                    correctAnswers++;
                }
            }
        }

        // Tính điểm theo phần trăm dựa trên TỔNG SỐ CÂU HỎI TRONG ĐỀ THI, không phải số câu đã trả lời
        float score = totalQuestionsInExam > 0 ? Math.round(((float) correctAnswers / totalQuestionsInExam) * 100) : 0;
        EnumClass.ExamStatus status = score >= 50.0 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED;

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
}
