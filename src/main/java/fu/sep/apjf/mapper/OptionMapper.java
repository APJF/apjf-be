package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;

public final class OptionMapper {

    private OptionMapper() {
        // Private constructor to prevent instantiation
    }

    public static OptionResponseDto toResponseDto(Option option) {
        if (option == null) {
            return null;
        }

        return new OptionResponseDto(
                option.getId(),
                option.getContent(),
                option.getIsCorrect()
        );
    }

    public static Option toEntity(OptionRequestDto optionDto) {
        if (optionDto == null) {
            return null;
        }

        Option option = new Option();
        option.setId(optionDto.id());
        option.setContent(optionDto.content());
        option.setIsCorrect(optionDto.isCorrect());

        return option;
    }
}
