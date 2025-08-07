package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ExamResultRequestDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.ExamResult;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {QuestionResultMapper.class})
public interface ExamResultMapper {

    @Mapping(source = "id", target = "examResultId")
    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "exam.title", target = "examTitle")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "submittedAt", target = "submittedAt")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "details", target = "questionResults")
    ExamResultResponseDto toDto(ExamResult entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "details", ignore = true)
    ExamResult toEntity(ExamResultRequestDto dto);
}