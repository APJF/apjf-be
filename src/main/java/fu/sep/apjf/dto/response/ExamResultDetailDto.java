package fu.sep.apjf.dto.response;

public record ExamResultDetailDto(
        String id,
        String userAnswer,
        Boolean isCorrect,
        String questionId,
        String questionContent,
        String selectedOptionId,
        String correctAnswer
) {
}
