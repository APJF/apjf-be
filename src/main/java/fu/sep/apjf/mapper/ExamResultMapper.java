package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.dto.response.QuestionResultResponseDto;
import fu.sep.apjf.dto.response.ExamHistoryResponseDto;
import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.entity.ExamResultDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExamResultMapper {

    @Mapping(source = "examResult.id", target = "examResultId")
    @Mapping(source = "examResult.exam.id", target = "examId")
    @Mapping(source = "examResult.exam.title", target = "examTitle")
    @Mapping(source = "examResult.score", target = "score") // bạn đang thiếu dòng này
    @Mapping(source = "examResult.status", target = "status") // ánh xạ thêm dòng này
    @Mapping(source = "examResult.submittedAt", target = "submittedAt")
    @Mapping(source = "details", target = "questionResults")
    ExamResultResponseDto toResponseDto(ExamResult examResult, List<ExamResultDetail> details);

    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "question.content", target = "questionContent")
    @Mapping(source = "question.explanation", target = "explanation")
    @Mapping(source = "selectedOption.id", target = "selectedOptionId")
    @Mapping(source = "isCorrect", target = "isCorrect")
    QuestionResultResponseDto toQuestionResultDto(ExamResultDetail detail);

    @Mapping(source = "id", target = "examResultId")
    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "exam.title", target = "examTitle")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "submittedAt", target = "submittedAt")
    ExamHistoryResponseDto toHistoryDto(ExamResult result);
}