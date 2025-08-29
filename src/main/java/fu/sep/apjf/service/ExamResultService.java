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
    private final ProgressTrackingService progressTrackingService;

    @Transactional
    public ExamDetailResponseDto startExam(Long userId, String examId) {
        // Kiểm tra user đã có IN_PROGRESS chưa
        ExamResult existingResult = examResultRepository
                .findFirstByUser_IdAndExam_IdAndStatusOrderByStartedAtDesc(userId, examId, EnumClass.ExamStatus.IN_PROGRESS)
                .orElse(null);

        if (existingResult != null) {
            return buildExamDetailResponseFromExistingResult(existingResult.getExam().getId());
        }

        // Lấy Exam cơ bản
        Exam exam = examRepository.findByIdOnly(examId)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND));

        // Lấy Questions + Options
        List<Question> questionsWithOptions = examRepository.findQuestionsByExamIdWithOptions(examId);
        exam.setQuestions(questionsWithOptions);

        // User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Tạo ExamResult IN_PROGRESS
        ExamResult result = ExamResult.builder()
                .exam(exam)
                .user(user)
                .startedAt(Instant.now())
                .status(EnumClass.ExamStatus.IN_PROGRESS)
                .build();

        // Tạo ExamResultDetail
        List<ExamResultDetail> details = questionsWithOptions.stream()
                .map(question -> {
                    ExamResultDetail detail = new ExamResultDetail();
                    detail.setExamResult(result);
                    detail.setQuestion(question);
                    return detail;
                })
                .toList();

        // Gán detail vào result
        result.setDetails(details);

        // Lưu result và detail
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

        // Lấy ExamResult đang IN_PROGRESS nếu có, nếu không tạo mới
        ExamResult result = examResultRepository
                .findFirstByUser_IdAndExam_IdAndStatusOrderByStartedAtDesc(userId, exam.getId(), EnumClass.ExamStatus.IN_PROGRESS)
                .orElseGet(() -> {
                    ExamResult newResult = ExamResult.builder()
                            .exam(exam)
                            .user(user)
                            .startedAt(dto.startedAt())
                            .status(EnumClass.ExamStatus.IN_PROGRESS)
                            .build();
                    return examResultRepository.save(newResult);
                });

        // Map questionId -> selectedOptionId, giữ value đầu tiên nếu trùng
        Map<String, String> selectedMap = dto.questionResults().stream()
                .filter(q -> q.selectedOptionId() != null)
                .collect(Collectors.toMap(
                        QuestionResultRequestDto::questionId,
                        QuestionResultRequestDto::selectedOptionId,
                        (existing, replacement) -> existing
                ));

        // Lấy Option từ database
        List<String> selectedOptionIds = selectedMap.values().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Option> optionMap = optionRepository.findAllById(selectedOptionIds)
                .stream()
                .collect(Collectors.toMap(Option::getId, o -> o));

        // Lấy tất cả ExamResultDetail hiện có
        List<ExamResultDetail> existingDetails = examResultDetailRepository.findByExamResultId(result.getId());

        for (Question question : exam.getQuestions()) {
            String selectedOptionId = selectedMap.get(question.getId());
            Option selectedOption = selectedOptionId != null ? optionMap.get(selectedOptionId) : null;
            boolean isCorrect = selectedOption != null && Boolean.TRUE.equals(selectedOption.getIsCorrect());

            // Tìm detail hiện có cho question
            ExamResultDetail detail = existingDetails.stream()
                    .filter(d -> d.getQuestion().getId().equals(question.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        ExamResultDetail newDetail = new ExamResultDetail();
                        newDetail.setExamResult(result);
                        newDetail.setQuestion(question);
                        existingDetails.add(newDetail);
                        return newDetail;
                    });

            detail.setSelectedOption(selectedOption);
            detail.setIsCorrect(isCorrect);
        }

        // Tính score
        int totalQuestions = existingDetails.size();
        int correctAnswers = (int) existingDetails.stream().filter(ExamResultDetail::getIsCorrect).count();
        float score = totalQuestions > 0 ? ((float) correctAnswers / totalQuestions) * 100 : 0.0f;

        result.setScore(score);
        result.setSubmittedAt(dto.submittedAt());
        result.setStatus(score >= 60 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED);

        // Lưu detail và result
        examResultDetailRepository.saveAll(existingDetails);
        examResultRepository.save(result);

        // Nếu pass, update progress
        if (result.getStatus() == EnumClass.ExamStatus.PASSED) {
            switch (exam.getExamScopeType()) {
                case UNIT -> progressTrackingService.markUnitPassed(exam.getUnit().getId(), userId);
                case CHAPTER -> progressTrackingService.markChapterComplete(exam.getChapter().getId(), userId);
                case COURSE -> progressTrackingService.markCourseComplete(exam.getCourse().getId(), userId);
                default -> throw new UnsupportedOperationException("Unsupported exam scope type.");
            }
        }

        return result.getId();
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
