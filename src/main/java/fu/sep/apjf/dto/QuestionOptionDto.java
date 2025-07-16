package fu.sep.apjf.dto;

public record QuestionOptionDto(
        String id,
        String content,
        Boolean isCorrect
) {
}
