package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ExamResultDetailDto;
import fu.sep.apjf.dto.ExamResultDto;
import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.entity.ExamResultDetail;

import java.util.List;

public final class ExamResultMapper {

    private ExamResultMapper() {
    }

    public static ExamResultDto toDto(ExamResult examResult) {
        if (examResult == null) {
            return null;
        }

        List<ExamResultDetailDto> answers = examResult.getDetails().stream()
                .map(detail -> new ExamResultDetailDto(
                        detail.getId(),
                        detail.getUserAnswer(),
                        detail.getIsCorrect(),
                        detail.getQuestion().getId(),
                        detail.getQuestion().getContent(),
                        detail.getSelectedOption() != null ? detail.getSelectedOption().getId() : null,
                        detail.getQuestion().getCorrectAnswer()
                ))
                .toList();

        int correctAnswers = (int) examResult.getDetails().stream()
                .filter(ExamResultDetail::getIsCorrect)
                .count();

        int totalQuestions = examResult.getExam().getQuestions().size();

        return new ExamResultDto(
                examResult.getId(),
                examResult.getStartedAt(),
                examResult.getSubmittedAt(),
                examResult.getScore(),
                examResult.getStatus(),
                examResult.getUser().getId().toString(),
                examResult.getExam().getId(),
                examResult.getExam().getTitle(),
                answers,
                totalQuestions,
                correctAnswers
        );
    }

    public static List<ExamResultDto> toDtoList(List<ExamResult> examResults) {
        if (examResults == null) {
            return List.of();
        }
        return examResults.stream()
                .map(ExamResultMapper::toDto)
                .toList();
    }
}
