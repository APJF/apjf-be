package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.dto.QuestionOptionDto;
import fu.sep.apjf.entity.Question;

import java.util.List;

public final class QuestionMapper {

    private QuestionMapper() {
        // Private constructor to prevent instantiation
    }

    public static QuestionDto toDto(Question question) {
        if (question == null) {
            return null;
        }

        List<QuestionOptionDto> optionDtos = null;
        if (question.getOptions() != null) {
            optionDtos = question.getOptions().stream()
                    .map(option -> new QuestionOptionDto(
                            option.getId(),
                            option.getContent(),
                            option.getIsCorrect()
                    ))
                    .toList();
        }

        return new QuestionDto(
                question.getId(),
                question.getContent(),
                question.getCorrectAnswer(),
                question.getType(),
                question.getScope(),
                question.getExplanation(),
                question.getFileUrl(),
                question.getCreatedAt(),
                optionDtos
        );
    }

    public static Question toEntity(QuestionDto questionDto) {
        if (questionDto == null) {
            return null;
        }

        Question question = new Question();
        question.setId(questionDto.id());
        question.setContent(questionDto.content());
        question.setCorrectAnswer(questionDto.correctAnswer());
        question.setType(questionDto.type());
        question.setScope(questionDto.scope());
        question.setExplanation(questionDto.explanation());
        question.setFileUrl(questionDto.fileUrl());

        return question;
    }

    public static List<QuestionDto> toDtoList(List<Question> questions) {
        if (questions == null) {
            return List.of();
        }

        return questions.stream()
                .map(QuestionMapper::toDto)
                .toList();
    }
}
