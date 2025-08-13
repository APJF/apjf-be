package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamResultRequestDto;
import fu.sep.apjf.dto.request.QuestionResultRequestDto;
import fu.sep.apjf.dto.response.ExamHistoryResponseDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamResultMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamResultService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamResultDetailRepository examResultDetailRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ExamResultMapper examResultMapper;
    private final UserRepository userRepository;

    public ExamResultResponseDto startExam(Long userId, String examId) {
        Exam exam = examRepository.findByIdWithQuestions(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ExamResult result = ExamResult.builder()
                .exam(exam)
                .user(user)
                .startedAt(Instant.now())
                .status(EnumClass.ExamStatus.IN_PROGRESS)
                .build();
        List<ExamResultDetail> details = exam.getQuestions().stream()
                .map(q -> {
                    ExamResultDetail d = new ExamResultDetail();
                    d.setExamResult(result);
                    d.setQuestion(q);
                    return d;
                })
                .toList();
        result.setDetails(details);
        examResultRepository.save(result);
        examResultDetailRepository.saveAll(details);
        return examResultMapper.toDto(result);
    }

    public ExamResultResponseDto submitExam(Long userId, ExamResultRequestDto dto) {
        Exam exam = examRepository.findById(dto.examId()).orElseThrow();

        if (exam.getType() != EnumClass.ExamType.MULTIPLE_CHOICE) {
            throw new UnsupportedOperationException("Only multiple choice exams are supported in this version.");
        }

        User user = userRepository.findById(userId).orElseThrow();

        ExamResult result = ExamResult.builder()
                .startedAt(dto.startedAt())
                .submittedAt(dto.submittedAt())
                .exam(exam)
                .user(user)
                .status(EnumClass.ExamStatus.SUBMITTED) // tạm thời, sẽ cập nhật sau
                .build();

        List<ExamResultDetail> details = new ArrayList<>();
        int totalQuestions = 0;
        int correctAnswers = 0;

        for (QuestionResultRequestDto questionDto : dto.questionResults()) {
            Question question = questionRepository.findById(questionDto.questionId()).orElseThrow();

            ExamResultDetail detail = new ExamResultDetail();
            detail.setExamResult(result);
            detail.setQuestion(question);

            totalQuestions++;

            if (questionDto.selectedOptionId() != null) {
                Option selectedOption = optionRepository.findById(questionDto.selectedOptionId()).orElse(null);
                detail.setSelectedOption(selectedOption);

                boolean isCorrect = selectedOption != null && Boolean.TRUE.equals(selectedOption.getIsCorrect());
                detail.setIsCorrect(isCorrect);
                if (isCorrect) {
                    correctAnswers++;
                }
            } else {
                detail.setIsCorrect(false); // Không chọn thì sai
            }

            details.add(detail);
        }

        float score = totalQuestions > 0 ? ((float) correctAnswers / totalQuestions) * 100 : 0.0f;
        result.setScore(score);

        result.setScore(score);
        result.setDetails(details);

        if (score >= 60.0) {
            result.setStatus(EnumClass.ExamStatus.PASSED);
        } else {
            result.setStatus(EnumClass.ExamStatus.FAILED);
        }

        ExamResult savedResult = examResultRepository.save(result);
        examResultDetailRepository.saveAll(details);

        return examResultMapper.toDto(savedResult);
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

    public ExamResultResponseDto getExamResult(Long resultId) {
        ExamResult result = examResultRepository.findByIdWithDetails(resultId).orElseThrow();
        return examResultMapper.toDto(result);
    }
}