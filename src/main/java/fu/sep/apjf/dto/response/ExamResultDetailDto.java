package fu.sep.apjf.dto.response;

public record ExamResultDetailDto(
        Long id,
        String userAnswer,
        Boolean isCorrect,
        String questionId,
        String questionContent,
        String selectedOptionId,
        String correctAnswer
) {
}
