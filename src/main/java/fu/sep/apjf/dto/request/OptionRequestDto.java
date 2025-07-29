package fu.sep.apjf.dto.request;

public record OptionRequestDto(
        String id,
        String content,
        Boolean isCorrect
) {
}
