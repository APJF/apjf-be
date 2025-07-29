package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Question;

import java.util.List;

public final class QuestionMapper {

    private QuestionMapper() {
        // Private constructor to prevent instantiation
    }

    public static QuestionResponseDto toResponseDto(Question question) {
        if (question == null) {
            return null;
        }

        List<OptionResponseDto> optionDtos = null;
        if (question.getOptions() != null) {
            optionDtos = question.getOptions().stream()
                    .map(option -> new OptionResponseDto(
                            option.getId(),
                            option.getContent(),
                            option.getIsCorrect()
                    ))
                    .toList();
        }

        return new QuestionResponseDto(
                question.getId(),
                question.getContent(),
                question.getCorrectAnswer(),
                question.getScope(),
                question.getType(),
                optionDtos
        );
    }

    public static Question toEntity(QuestionRequestDto questionDto) {
        if (questionDto == null) {
            return null;
        }

        Question question = new Question();
        question.setId(questionDto.id());
        question.setContent(questionDto.content());
        question.setCorrectAnswer(questionDto.correctAnswer());
        question.setType(questionDto.type());
        question.setScope(questionDto.scope());

        return question;
    }

    public static List<QuestionResponseDto> toResponseDtoList(List<Question> questions) {
        if (questions == null) {
            return List.of();
        }

        return questions.stream()
                .map(QuestionMapper::toResponseDto)
                .toList();
    }
}
