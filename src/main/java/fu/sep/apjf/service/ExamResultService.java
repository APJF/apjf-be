package fu.sep.apjf.service;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.entity.*;
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
    private final ExamResultAnswerRepository examResultAnswerRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public ExamResult startExam(StartExamDto startExamDto) {
        // Kiểm tra exam có tồn tại không
        Exam exam = examRepository.findById(startExamDto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found: " + startExamDto.getExamId()));

        // Kiểm tra user đã làm bài này chưa
        examResultRepository.findByUserIdAndExamId(startExamDto.getUserId(), startExamDto.getExamId())
                .ifPresent(existingResult -> {
                    if (existingResult.getSubmittedAt() == null) {
                        throw new RuntimeException("User is already taking this exam");
                    }
                });

        ExamResult examResult = ExamResult.builder()
                .id(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now())
                .userId(startExamDto.getUserId())
                .exam(exam)
                .build();

        return examResultRepository.save(examResult);
    }

    public ExamResult submitExam(SubmitExamDto submitExamDto) {
        ExamResult examResult = examResultRepository.findById(submitExamDto.getExamResultId())
                .orElseThrow(() -> new RuntimeException("Exam result not found: " + submitExamDto.getExamResultId()));

        if (examResult.getSubmittedAt() != null) {
            throw new RuntimeException("Exam already submitted");
        }

        // Lưu các câu trả lời
        int correctAnswers = 0;
        int totalQuestions = submitExamDto.getAnswers().size();

        for (AnswerSubmissionDto answerDto : submitExamDto.getAnswers()) {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + answerDto.getQuestionId()));

            boolean isCorrect = checkAnswer(question, answerDto);
            if (isCorrect) correctAnswers++;

            ExamResultAnswer answer = ExamResultAnswer.builder()
                    .id(UUID.randomUUID().toString())
                    .userAnswer(answerDto.getUserAnswer())
                    .isCorrect(isCorrect)
                    .examResult(examResult)
                    .question(question)
                    .build();

            if (answerDto.getSelectedOptionId() != null) {
                QuestionOption selectedOption = questionOptionRepository.findById(answerDto.getSelectedOptionId())
                        .orElse(null);
                answer.setSelectedOption(selectedOption);
            }

            examResultAnswerRepository.save(answer);
        }

        float score = (float) correctAnswers / totalQuestions * 10;
        EnumClass.ExamStatus status = score >= 5.0 ? EnumClass.ExamStatus.PASSED : EnumClass.ExamStatus.FAILED;

        examResult.setSubmittedAt(LocalDateTime.now());
        examResult.setScore(score);
        examResult.setStatus(status);

        return examResultRepository.save(examResult);
    }

    private boolean checkAnswer(Question question, AnswerSubmissionDto answerDto) {
        switch (question.getType()) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                if (answerDto.getSelectedOptionId() != null) {
                    QuestionOption selectedOption = questionOptionRepository.findById(answerDto.getSelectedOptionId())
                            .orElse(null);
                    return selectedOption != null && selectedOption.getIsCorrect();
                }
                return false;

            case SHORT_ANSWER:
            case FILL_BLANK:
                return question.getCorrectAnswer() != null &&
                       question.getCorrectAnswer().equalsIgnoreCase(answerDto.getUserAnswer());

            default:
                return false;
        }
    }

    public List<ExamResult> getExamResultsByUserId(String userId) {
        return examResultRepository.findByUserId(userId);
    }

    public List<ExamResult> getExamResultsByExamId(String examId) {
        return examResultRepository.findByExamId(examId);
    }

    public ExamResult getExamResultById(String id) {
        return examResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam result not found: " + id));
    }

    public Double getAverageScoreByExamId(String examId) {
        return examResultRepository.getAverageScoreByExamId(examId);
    }

    public List<ExamResult> getInProgressExams() {
        return examResultRepository.findInProgressExams();
    }
}
