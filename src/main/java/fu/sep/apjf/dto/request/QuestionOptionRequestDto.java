package fu.sep.apjf.dto.request;

public record QuestionOptionRequestDto(
        String id,
        String content,
        Boolean isCorrect
) {
}
