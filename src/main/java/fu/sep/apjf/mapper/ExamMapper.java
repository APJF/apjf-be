package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Exam;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "questions", ignore = true)
    ExamResponseDto toDto(Exam exam);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Exam toEntity(ExamRequestDto dto);

    default ExamResponseDto toResponseDto(Exam exam, List<QuestionResponseDto> questions) {
        ExamResponseDto dto = toDto(exam);
        return ExamResponseDto.of(
                dto.id(), dto.title(), dto.description(), dto.duration(), dto.type(),
                dto.examScopeType(), dto.gradingMethod(),
                dto.courseId(), dto.chapterId(), dto.unitId(),
                exam.getCreatedAt(), questions
        );
    }
}
