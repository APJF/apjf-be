package fu.sep.apjf.dto;

public record AnswerSubmissionDto(
        String questionId,
        String userAnswer,
        String selectedOptionId

) {

}
