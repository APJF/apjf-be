package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ExamMapper {

    private ExamMapper() {
        // Private constructor to prevent instantiation
    }

    public static ExamDto toDto(Exam exam) {
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

        List<QuestionDto> questions = new ArrayList<>();
        List<String> questionIds = new ArrayList<>();
        int questionCount = 0;
        int totalQuestions = 0;

        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            questions = exam.getQuestions().stream()
                    .map(QuestionMapper::toDto)
                    .collect(Collectors.toList());

            questionIds = exam.getQuestions().stream()
                    .map(Question::getId)
                    .toList();

            questionCount = exam.getQuestions().size();
            totalQuestions = questionCount;
        }

        return new ExamDto(
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

    public static Exam toEntity(ExamDto examDto) {
        if (examDto == null) {
            return null;
        }

        Exam exam = new Exam();
        exam.setId(examDto.id());
        exam.setTitle(examDto.title());
        exam.setDescription(examDto.description());
        exam.setDuration(examDto.duration());
        exam.setExamScopeType(examDto.examScopeType());
        exam.setCreatedAt(examDto.createdAt());

        return exam;
    }

    public static List<ExamDto> toDtoList(List<Exam> exams) {
        if (exams == null) {
            return List.of();
        }
        return exams.stream()
                .map(ExamMapper::toDto)
                .toList();
    }
}
