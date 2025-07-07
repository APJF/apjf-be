package fu.sep.apjf.service;

import fu.sep.apjf.dto.CreateExamDto;
import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.repository.ExamRepository;
import fu.sep.apjf.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam getExamById(String id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + id));
    }

    public List<Exam> getExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        return examRepository.findByExamScopeType(scopeType);
    }

    public List<Exam> searchExams(String keyword) {
        return examRepository.findByTitleOrDescriptionContaining(keyword);
    }

    public Exam createExam(CreateExamDto createExamDto) {
        Exam exam = Exam.builder()
                .id(UUID.randomUUID().toString())
                .title(createExamDto.getTitle())
                .description(createExamDto.getDescription())
                .duration(createExamDto.getDuration())
                .examScopeType(createExamDto.getExamScopeType())
                .build();

        // Thêm questions vào exam
        if (createExamDto.getQuestionIds() != null && !createExamDto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(createExamDto.getQuestionIds());
            exam.setQuestions(questions);
        }

        return examRepository.save(exam);
    }

    public Exam updateExam(String id, CreateExamDto updateExamDto) {
        Exam exam = getExamById(id);

        exam.setTitle(updateExamDto.getTitle());
        exam.setDescription(updateExamDto.getDescription());
        exam.setDuration(updateExamDto.getDuration());
        exam.setExamScopeType(updateExamDto.getExamScopeType());

        // Cập nhật questions
        if (updateExamDto.getQuestionIds() != null) {
            List<Question> questions = questionRepository.findAllById(updateExamDto.getQuestionIds());
            exam.setQuestions(questions);
        }

        return examRepository.save(exam);
    }

    public void deleteExam(String id) {
        examRepository.deleteById(id);
    }

    public Exam addQuestionToExam(String examId, String questionId) {
        Exam exam = getExamById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));

        if (!exam.getQuestions().contains(question)) {
            exam.getQuestions().add(question);
            return examRepository.save(exam);
        }

        return exam;
    }

    public Exam removeQuestionFromExam(String examId, String questionId) {
        Exam exam = getExamById(examId);
        exam.getQuestions().removeIf(question -> question.getId().equals(questionId));
        return examRepository.save(exam);
    }

    public ExamDto convertToDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDuration());
        dto.setExamScopeType(exam.getExamScopeType());
        dto.setCreatedAt(exam.getCreatedAt());
        dto.setTotalQuestions(exam.getQuestions().size());
        return dto;
    }
}
