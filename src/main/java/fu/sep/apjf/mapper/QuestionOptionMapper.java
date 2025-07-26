package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionOptionRequestDto;
import fu.sep.apjf.dto.response.QuestionOptionResponseDto;
import fu.sep.apjf.entity.QuestionOption;

public final class QuestionOptionMapper {

    private QuestionOptionMapper() {
        // Private constructor to prevent instantiation
    }

    public static QuestionOptionResponseDto toResponseDto(QuestionOption option) {
        if (option == null) {
            return null;
        }

        return new QuestionOptionResponseDto(
                option.getId(),
                option.getContent(),
                option.getIsCorrect()
        );
    }

    public static QuestionOption toEntity(QuestionOptionRequestDto optionDto) {
        if (optionDto == null) {
            return null;
        }

        QuestionOption option = new QuestionOption();
        option.setId(optionDto.id());
        option.setContent(optionDto.content());
        option.setIsCorrect(optionDto.isCorrect());

        return option;
    }
}
