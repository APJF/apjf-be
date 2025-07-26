package fu.sep.apjf.dto.response;

public record QuestionOptionResponseDto(
        String id,
        String content,
        Boolean isCorrect
) {
}
