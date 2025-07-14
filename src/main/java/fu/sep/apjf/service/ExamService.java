package fu.sep.apjf.service;

import fu.sep.apjf.dto.CreateExamDto;
import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.dto.QuestionOptionDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.DuplicateResourceException;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExamService {

    private static final String EXAM_ID_NULL_MESSAGE = "Exam ID must not be null";
    private static final String EXAM_NOT_FOUND_PREFIX = "Exam not found with ID: ";
    private static final String QUESTION_NOT_FOUND_PREFIX = "Question not found with ID: ";
    private static final String COURSE_NOT_FOUND_PREFIX = "Course not found with ID: ";
    private static final String TITLE_EXISTS_PREFIX = "Exam with title already exists: ";

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final UnitRepository unitRepository;

    /**
     * Basic CRUD Operations
     */

    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Exam getExamById(String id) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);

        // Bước 1: Lấy exam với các questions đã được eager load
        Exam exam = examRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND_PREFIX + id));

        // Bước 2: Tải options cho mỗi question
        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            for (Question question : exam.getQuestions()) {
                // Truy cập collection options để force Hibernate tải chúng
                question.getOptions().size();
            }
        }

        return exam;
    }

    /**
     * Finder Methods Using Updated Repository
     */

    @Transactional(readOnly = true)
    public List<Exam> getExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        Objects.requireNonNull(scopeType, "Scope type must not be null");
        return examRepository.findByExamScopeType(scopeType);
    }

    @Transactional(readOnly = true)
    public List<Exam> getExamsByCourse(String courseId) {
        Objects.requireNonNull(courseId, "Course ID must not be null");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        return examRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<Exam> getExamsByChapter(String chapterId) {
        Objects.requireNonNull(chapterId, "Chapter ID must not be null");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with ID: " + chapterId));
        return examRepository.findByChapter(chapter);
    }

    @Transactional(readOnly = true)
    public List<Exam> getExamsByUnit(String unitId) {
        Objects.requireNonNull(unitId, "Unit ID must not be null");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with ID: " + unitId));
        return examRepository.findByUnit(unit);
    }

    @Transactional(readOnly = true)
    public List<Exam> searchExamsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return examRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional(readOnly = true)
    public List<Exam> searchExams(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return examRepository.searchByTitleOrDescription(keyword);
    }

    @Transactional(readOnly = true)
    public List<Exam> getExamsByDurationRange(Double minDuration, Double maxDuration) {
        Objects.requireNonNull(minDuration, "Minimum duration must not be null");
        Objects.requireNonNull(maxDuration, "Maximum duration must not be null");
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Minimum duration must be less than or equal to maximum duration");
        }
        return examRepository.findByDurationBetween(minDuration, maxDuration);
    }

    /**
     * Counting and Statistics
     */

    @Transactional(readOnly = true)
    public long countExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        Objects.requireNonNull(scopeType, "Scope type must not be null");
        return examRepository.countByExamScopeType(scopeType);
    }

    @Transactional(readOnly = true)
    public long countExamsByCourse(String courseId) {
        Objects.requireNonNull(courseId, "Course ID must not be null");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        return examRepository.countByCourse(course);
    }

    @Transactional(readOnly = true)
    public boolean hasExamsForCourse(String courseId) {
        Objects.requireNonNull(courseId, "Course ID must not be null");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        return examRepository.existsByCourse(course);
    }

    /**
     * Create/Update Operations
     */

    public Exam createExam(CreateExamDto createExamDto) {
        Objects.requireNonNull(createExamDto, "Exam data must not be null");
        Objects.requireNonNull(createExamDto.getTitle(), "Exam title must not be null");

        log.info("Creating new exam with title: {}", createExamDto.getTitle());

        /* Validate unique title */
        if (examRepository.existsByTitleIgnoreCase(createExamDto.getTitle())) {
            throw new DuplicateResourceException(TITLE_EXISTS_PREFIX + createExamDto.getTitle());
        }

        Exam exam = Exam.builder()
                .id(UUID.randomUUID().toString())
                .title(createExamDto.getTitle())
                .description(createExamDto.getDescription())
                .duration(createExamDto.getDuration())
                .examScopeType(createExamDto.getExamScopeType())
                .build();

        /* Add questions to exam */
        addQuestionsToExam(exam, createExamDto.getQuestionIds());

        Exam savedExam = examRepository.save(exam);
        log.info("Successfully created exam with ID: {}", savedExam.getId());
        return savedExam;
    }

    public Exam updateExam(String id, CreateExamDto updateDto) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(updateDto, "Update data must not be null");
        Objects.requireNonNull(updateDto.getTitle(), "Exam title must not be null");

        log.info("Updating exam with ID: {}", id);

        Exam exam = getExamById(id);

        /* Check title uniqueness if changed */
        if (!exam.getTitle().equalsIgnoreCase(updateDto.getTitle()) &&
                examRepository.existsByTitleIgnoreCase(updateDto.getTitle())) {
            throw new DuplicateResourceException(TITLE_EXISTS_PREFIX + updateDto.getTitle());
        }

        exam.setTitle(updateDto.getTitle());
        exam.setDescription(updateDto.getDescription());
        exam.setDuration(updateDto.getDuration());
        exam.setExamScopeType(updateDto.getExamScopeType());

        /* Update questions */
        exam.getQuestions().clear();
        addQuestionsToExam(exam, updateDto.getQuestionIds());

        Exam updatedExam = examRepository.save(exam);
        log.info("Successfully updated exam with ID: {}", updatedExam.getId());
        return updatedExam;
    }

    public void deleteExam(String id) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);

        log.info("Deleting exam with ID: {}", id);

        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException(EXAM_NOT_FOUND_PREFIX + id);
        }
        examRepository.deleteById(id);
        log.info("Successfully deleted exam with ID: {}", id);
    }

    /**
     * Question Management
     */

    public Exam addQuestionToExam(String examId, String questionId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(questionId, "Question ID must not be null");

        log.info("Adding question ID: {} to exam ID: {}", questionId, examId);

        Exam exam = getExamById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + questionId));

        if (!exam.getQuestions().contains(question)) {
            exam.getQuestions().add(question);
            return examRepository.save(exam);
        }
        return exam;
    }

    public Exam removeQuestionFromExam(String examId, String questionId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(questionId, "Question ID must not be null");

        log.info("Removing question ID: {} from exam ID: {}", questionId, examId);

        Exam exam = getExamById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + questionId));

        exam.getQuestions().remove(question);
        return examRepository.save(exam);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsForExam(String examId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Exam exam = getExamById(examId);
        return questionRepository.findByExamsContaining(exam);
    }

    @Transactional(readOnly = true)
    public List<Exam> getExamsWithoutQuestions() {
        return examRepository.findExamsWithoutQuestions();
    }

    /**
     * Convert Exam entity to ExamDto
     *
     * @param exam the exam entity to convert
     * @return the converted exam DTO
     */
    public ExamDto convertToDto(Exam exam) {
        Objects.requireNonNull(exam, "Exam must not be null");

        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDuration());
        dto.setExamScopeType(exam.getExamScopeType());
        dto.setCreatedAt(exam.getCreatedAt());

        // Convert the questions collection
        if (exam.getQuestions() != null) {
            dto.setQuestions(exam.getQuestions().stream()
                    .map(this::convertQuestionToDto)
                    .toList());
            dto.setTotalQuestions(exam.getQuestions().size());
        } else {
            dto.setQuestions(Collections.emptyList());
            dto.setTotalQuestions(0);
        }

        return dto;
    }

    /**
     * Convert Question entity to QuestionDto
     *
     * @param question the question entity to convert
     * @return the converted question DTO
     */
    private QuestionDto convertQuestionToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setType(question.getType());
        dto.setExplanation(question.getExplanation());
        dto.setFileUrl(question.getFileUrl());
        dto.setCreatedAt(question.getCreatedAt());

        // Convert question options if available
        if (question.getOptions() != null) {
            dto.setOptions(question.getOptions().stream()
                    .map(this::convertOptionToDto)
                    .toList());
        }

        return dto;
    }

    /**
     * Convert QuestionOption entity to QuestionOptionDto
     *
     * @param option the question option entity to convert
     * @return the converted question option DTO
     */
    private QuestionOptionDto convertOptionToDto(QuestionOption option) {
        QuestionOptionDto dto = new QuestionOptionDto();
        dto.setId(option.getId());
        dto.setContent(option.getContent());
        dto.setIsCorrect(option.getIsCorrect());
        return dto;
    }

    /**
     * Helper method to add questions to an exam
     *
     * @param exam        the exam to add questions to
     * @param questionIds the list of question IDs to add
     */
    private void addQuestionsToExam(Exam exam, List<String> questionIds) {
        if (questionIds != null && !questionIds.isEmpty()) {
            List<Question> questions = questionRepository.findAllById(questionIds);
            exam.getQuestions().addAll(questions);
        }
    }

    /**
     * Lấy tất cả các bài kiểm tra dưới dạng DTO để tránh LazyInitializationException
     *
     * @return Danh sách tất cả các bài kiểm tra dưới dạng DTO
     */
    @Transactional(readOnly = true)
    public List<ExamDto> getAllExamsAsDto() {
        List<Exam> exams = examRepository.findAll();
        return exams.stream()
                .map(this::convertToDto)
                .toList();
    }
}
