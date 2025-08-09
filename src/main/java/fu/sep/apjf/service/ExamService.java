package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamOverviewResponseDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamMapper;
import fu.sep.apjf.mapper.ExamOverviewMapper;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.ExamRepository;
import fu.sep.apjf.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private static final String NOT_FOUND_EXAM_MSG = "Không tìm thấy exam";

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamMapper examMapper;
    private final ExamOverviewMapper examOverviewMapper;
    private final QuestionMapper questionMapper;

    public ExamResponseDto create(ExamRequestDto dto) {
        validateExamScope(dto);
        Exam exam = examMapper.toEntity(dto);
        return examMapper.toDto(examRepository.save(exam));
    }

    public ExamResponseDto update(String id, ExamRequestDto dto) {
        Exam existing = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        validateExamScope(dto);
        Exam updated = examMapper.toEntity(dto);
        updated.setId(existing.getId());
        return examMapper.toDto(examRepository.save(updated));
    }

    public void delete(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        examRepository.delete(exam);
    }

    public void addQuestions(String examId, List<String> questionIds) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        List<Question> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều câu hỏi không tồn tại");
        }
        exam.getQuestions().addAll(questions);
        examRepository.save(exam);
    }

    public void removeQuestions(String examId, List<String> questionIds) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        exam.getQuestions().removeIf(q -> questionIds.contains(q.getId()));
        examRepository.save(exam);
    }

    public ExamOverviewResponseDto getOverview(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        return examOverviewMapper.toDto(exam);
    }

    public ExamResponseDto getExamDetail(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        return examMapper.toDto(exam);
    }

    public List<ExamResponseDto> findAll() {
        return examRepository.findAll().stream()
                .map(examMapper::toDto)
                .toList();
    }

    private void validateExamScope(ExamRequestDto dto) {
        String missingField = switch (dto.examScopeType()) {
            case COURSE -> (dto.courseId() == null || dto.courseId().isBlank()) ? "courseId" : null;
            case CHAPTER -> (dto.chapterId() == null || dto.chapterId().isBlank()) ? "chapterId" : null;
            case UNIT -> (dto.unitId() == null || dto.unitId().isBlank()) ? "unitId" : null;
        };
        if (missingField != null) {
            throw new IllegalArgumentException(missingField + " is required for " + dto.examScopeType() + " scope");
        }
    }

    @Transactional
    public List<fu.sep.apjf.dto.response.QuestionResponseDto> getQuestionsByExamId(String examId) {
        Exam exam = examRepository.findByIdWithQuestions(examId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_EXAM_MSG));
        return exam.getQuestions().stream()
                .map(questionMapper::toDto)
                .toList();
    }

}