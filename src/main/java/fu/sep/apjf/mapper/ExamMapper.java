package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ExamMapper {

    private ExamMapper() {
        // Private constructor to prevent instantiation
    }

    public static ExamResponseDto toResponseDto(Exam exam) {
        if (exam == null) {
            return null;
        }

        String courseId = null;
        if (exam.getCourse() != null) {
            courseId = exam.getCourse().getId();
        }

        String chapterId = null;
        if (exam.getChapter() != null) {
            chapterId = exam.getChapter().getId();
        }

        String unitId = null;
        if (exam.getUnit() != null) {
            unitId = exam.getUnit().getId();
        }

        List<QuestionResponseDto> questions = new ArrayList<>();
        List<String> questionIds = new ArrayList<>();
        int questionCount = 0;
        int totalQuestions = 0;

        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            questions = exam.getQuestions().stream()
                    .map(QuestionMapper::toResponseDto)
                    .collect(Collectors.toList());

            questionIds = exam.getQuestions().stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());

            questionCount = exam.getQuestions().size();
            totalQuestions = questionCount;
        }

        return new ExamResponseDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getDuration(),
                exam.getExamScopeType(),
                exam.getCreatedAt(),
                questions,
                totalQuestions,
                courseId,
                chapterId,
                unitId,
                questionIds,
                questionCount
        );
    }

    public static Exam toEntity(ExamRequestDto examDto) {
        if (examDto == null) {
            return null;
        }

        Exam exam = new Exam();
        exam.setId(UUID.randomUUID().toString());
        exam.setTitle(examDto.title());
        exam.setDescription(examDto.description());
        exam.setDuration(examDto.duration());
        exam.setExamScopeType(examDto.examScopeType());

        return exam;
    }

    public static List<ExamResponseDto> toResponseDtoList(List<Exam> exams) {
        if (exams == null) {
            return List.of();
        }
        return exams.stream()
                .map(ExamMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
