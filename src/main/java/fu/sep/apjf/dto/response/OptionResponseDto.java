package fu.sep.apjf.dto.response;

public record OptionResponseDto(
        String id,
        String content,
        Boolean isCorrect
) {
}
