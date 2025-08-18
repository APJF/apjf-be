package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamResultRequestDto;
import fu.sep.apjf.dto.request.QuestionResultRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamResultMapper;
import fu.sep.apjf.mapper.QuestionResultMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamResultService {

    private static final String EXAM_NOT_FOUND = "Exam not found";

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamResultDetailRepository examResultDetailRepository;
    private final OptionRepository optionRepository;
    private final QuestionResultMapper questionResultMapper;
    private final ExamResultDetailRepository detailRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExamDetailResponseDto startExam(Long userId, String examId) {
        // Kiểm tra xem user đã bắt đầu exam này chưa (idempotent check)
        ExamResult existingResult = examResultRepository
                .findByUserIdAndExamIdAndStatus(userId, examId, EnumClass.ExamStatus.IN_PROGRESS);

        if (existingResult != null) {
            // Nếu đã có exam đang IN_PROGRESS, trả về thông tin exam đó
            return buildExamDetailResponseFromExistingResult(existingResult.getExam().getId());
        }

        // Giải pháp 2-query để tránh MultipleBagFetchException:
        // Query 1: Lấy Exam cơ bản
        Exam exam = examRepository.findByIdOnly(examId)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND));

        // Query 2: Lấy Questions với Options riêng biệt
        List<Question> questionsWithOptions = examRepository.findQuestionsByExamIdWithOptions(examId);
        exam.setQuestions(questionsWithOptions);

        // User lookup đơn giản
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Tạo ExamResult
        ExamResult result = ExamResult.builder()
                .exam(exam)
                .user(user)
                .startedAt(Instant.now())
                .status(EnumClass.ExamStatus.IN_PROGRESS)
                .build();

        // Batch create details
        List<ExamResultDetail> details = questionsWithOptions.stream()
                .map(question -> {
                    ExamResultDetail detail = new ExamResultDetail();
                    detail.setExamResult(result);
                    detail.setQuestion(question);
                    return detail;
                })
                .toList();

        result.setDetails(details);

        // Single transaction với batch operations
        examResultRepository.save(result);
        if (!details.isEmpty()) {
            examResultDetailRepository.saveAll(details);
        }

        return buildExamDetailResponseOptimized(exam, questionsWithOptions);
    }

    private ExamDetailResponseDto buildExamDetailResponseFromExistingResult(String examId) {
        // Cho trường hợp idempotent, fetch data với 2-query approach
        Exam exam = examRepository.findByIdOnly(examId)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND));
        List<Question> questions = examRepository.findQuestionsByExamIdWithOptions(examId);
        return buildExamDetailResponseOptimized(exam, questions);
    }

    private ExamDetailResponseDto buildExamDetailResponseOptimized(Exam exam, List<Question> questions) {
        // Convert với data đã được prefetch (Questions + Options)
        List<QuestionResponseDto> questionDtos = questions.stream()
                .map(this::convertQuestionToDtoOptimized)
                .toList();

        return new ExamDetailResponseDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getDuration(),
                exam.getType(),
                exam.getExamScopeType(),
                exam.getGradingMethod(),
                exam.getCourse() != null ? exam.getCourse().getId() : null,
                exam.getChapter() != null ? exam.getChapter().getId() : null,
                exam.getUnit() != null ? exam.getUnit().getId() : null,
                exam.getCreatedAt(),
                questionDtos
        );
    }

    private QuestionResponseDto convertQuestionToDtoOptimized(Question question) {
        // Options đã được eager fetch → zero additional queries
        List<OptionExamResponseDto> options = question.getOptions().stream()
                .map(option -> new OptionExamResponseDto(
                        option.getId(),
                        option.getContent()
                ))
                .toList();

        // Không load units để tránh lazy loading
        List<String> unitIds = List.of();

        return new QuestionResponseDto(
                question.getId(),
                question.getContent(),
                question.getScope(),
                question.getType(),
                question.getFileUrl(),
                question.getCreatedAt(),
                options,
                unitIds
        );
    }



    @Transactional
    public Long submitExam(Long userId, ExamResultRequestDto dto) {
        // Lấy exam cùng questions
        Exam exam = examRepository.findByIdWithQuestions(dto.examId())
                .orElseThrow(() -> new EntityNotFoundException(EXAM_NOT_FOUND));

        if (exam.getType() != EnumClass.ExamType.MULTIPLE_CHOICE) {
            throw new UnsupportedOperationException("Only multiple choice exams are supported in this version.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ExamResult result = ExamResult.builder()
                .startedAt(dto.startedAt())
                .submittedAt(dto.submittedAt())
                .exam(exam)
                .user(user)
                .status(EnumClass.ExamStatus.SUBMITTED) // tạm thời
                .build();

        // Map questionId -> selectedOptionId từ DTO
        Map<String, String> selectedMap = dto.questionResults().stream()
                .collect(Collectors.toMap(QuestionResultRequestDto::questionId, QuestionResultRequestDto::selectedOptionId));

        // Lấy tất cả option cần dùng trong 1 query
        List<String> selectedOptionIds = selectedMap.values().stream()
                .filter(Objects::nonNull)
                .toList();

        Map<String, Option> optionMap = optionRepository.findAllById(selectedOptionIds)
                .stream()
                .collect(Collectors.toMap(Option::getId, o -> o));

        List<ExamResultDetail> details = new ArrayList<>();
        int totalQuestions = 0;
        int correctAnswers = 0;

        for (Question question : exam.getQuestions()) {
            ExamResultDetail detail = new ExamResultDetail();
            detail.setExamResult(result);
            detail.setQuestion(question);

            totalQuestions++;

            String selectedOptionId = selectedMap.get(question.getId());
            Option selectedOption = selectedOptionId != null ? optionMap.get(selectedOptionId) : null;
            detail.setSelectedOption(selectedOption);

            boolean isCorrect = selectedOption != null && Boolean.TRUE.equals(selectedOption.getIsCorrect());
            detail.setIsCorrect(isCorrect);
            if (isCorrect) correctAnswers++;

            details.add(detail);
        }

        // Tính điểm
        float score = totalQuestions > 0 ? ((float) correctAnswers / totalQuestions) * 100 : 0.0f;
        result.setScore(score);
        result.setStatus(score >= 60.0 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED);

        // Lưu ExamResult
        ExamResult examResult = examResultRepository.save(result);

        // Batch insert tất cả ExamResultDetail
        examResultDetailRepository.saveAll(details);

        return examResult.getId();
    }

    public List<ExamHistoryResponseDto> getHistoryByUserId(Long userId) {
        return examResultRepository.findByUserIdWithExam(userId)
                .stream()
                .filter(r -> r.getStatus() != EnumClass.ExamStatus.IN_PROGRESS) // loại bỏ đang làm dở
                .map(r -> new ExamHistoryResponseDto(
                        String.valueOf(r.getId()),
                        r.getExam().getId(),
                        r.getExam().getTitle(),
                        r.getScore(),
                        r.getStatus(),
                        r.getExam().getType(),
                        r.getSubmittedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamResultResponseDto getExamResultDetails(Long examResultId) {
        ExamResult examResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new EntityNotFoundException("ExamResult not found"));

        List<QuestionResultResponseDto> details = detailRepository.findByExamResultIdWithOptions(examResultId)
                .stream()
                .map(questionResultMapper::toDto)
                .toList();

        return new ExamResultResponseDto(
                examResult.getId(),
                examResult.getExam().getId(),
                examResult.getExam().getTitle(),
                examResult.getScore(),
                examResult.getSubmittedAt(),
                examResult.getStatus(),
                details
        );
    }

}
