package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.QuestionOptionDto;
import fu.sep.apjf.entity.QuestionOption;

public final class QuestionOptionMapper {

    private QuestionOptionMapper() {
        // Private constructor to prevent instantiation
    }

    public static QuestionOptionDto toDto(QuestionOption option) {
        if (option == null) {
            return null;
        }

        return new QuestionOptionDto(
                option.getId(),
                option.getContent(),
                option.getIsCorrect()
        );
    }

    public static QuestionOption toEntity(QuestionOptionDto optionDto) {
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
