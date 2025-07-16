package fu.sep.apjf.service;

import fu.sep.apjf.dto.CreateExamDto;
import fu.sep.apjf.dto.ExamDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.DuplicateResourceException;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamMapper;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExamService {

    private static final String EXAM_ID_NULL_MESSAGE = "Mã đề thi không được để trống";
    private static final String EXAM_NOT_FOUND_PREFIX = "Không tìm thấy đề thi với mã: ";
    private static final String QUESTION_NOT_FOUND_PREFIX = "Không tìm thấy câu hỏi với mã: ";
    private static final String COURSE_NOT_FOUND_PREFIX = "Không tìm thấy khóa học với mã: ";
    private static final String CHAPTER_NOT_FOUND_PREFIX = "Không tìm thấy chương với mã: ";
    private static final String UNIT_NOT_FOUND_PREFIX = "Không tìm thấy bài học với mã: ";
    private static final String TITLE_EXISTS_PREFIX = "Đề thi với tiêu đề này đã tồn tại: ";

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final UnitRepository unitRepository;

    /**
     * Basic CRUD Operations
     */

    @Transactional(readOnly = true)
    public List<ExamDto> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getAllExams(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return examRepository.findAll(pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ExamDto getExamById(String id) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);

        Exam exam = examRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND_PREFIX + id));

        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            for (Question question : exam.getQuestions()) {
                // Truy cập collection options để force Hibernate tải chúng
                question.getOptions().size();
            }
        }

        return ExamMapper.toDto(exam);
    }

    /**
     * Finder Methods Using Updated Repository
     */

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        Objects.requireNonNull(scopeType, "Phạm vi đề thi không được để trống");
        List<Exam> exams = examRepository.findByExamScopeType(scopeType);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getExamsByScopeType(EnumClass.ExamScopeType scopeType, int page, int size) {
        Objects.requireNonNull(scopeType, "Phạm vi đề thi không được để trống");
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByExamScopeType(scopeType, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByCourse(String courseId) {
        Objects.requireNonNull(courseId, "Mã khóa học không được để trống");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        List<Exam> exams = examRepository.findByCourse(course);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getExamsByCourse(String courseId, int page, int size) {
        Objects.requireNonNull(courseId, "Mã khóa học không được để trống");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByCourse(course, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByChapter(String chapterId) {
        Objects.requireNonNull(chapterId, "Mã chương không được để trống");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAPTER_NOT_FOUND_PREFIX + chapterId));
        List<Exam> exams = examRepository.findByChapter(chapter);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getExamsByChapter(String chapterId, int page, int size) {
        Objects.requireNonNull(chapterId, "Mã chương không được để trống");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAPTER_NOT_FOUND_PREFIX + chapterId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByChapter(chapter, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByUnit(String unitId) {
        Objects.requireNonNull(unitId, "Mã bài học không được để trống");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        List<Exam> exams = examRepository.findByUnit(unit);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getExamsByUnit(String unitId, int page, int size) {
        Objects.requireNonNull(unitId, "Mã bài học không được để trống");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByUnit(unit, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> searchExamsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Exam> exams = examRepository.findByTitleContainingIgnoreCase(title);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> searchExamsByTitle(String title, int page, int size) {
        if (title == null || title.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByTitleContainingIgnoreCase(title, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> searchExams(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Exam> exams = examRepository.searchByTitleOrDescription(keyword);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> searchExams(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.searchByTitleOrDescription(keyword, pageable).map(ExamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByDurationRange(Double minDuration, Double maxDuration) {
        Objects.requireNonNull(minDuration, "Thời lượng tối thiểu không được để trống");
        Objects.requireNonNull(maxDuration, "Thời lượng tối đa không được để trống");
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Thời lượng tối thiểu phải nhỏ hơn hoặc bằng thời lượng tối đa");
        }
        List<Exam> exams = examRepository.findByDurationBetween(minDuration, maxDuration);
        return ExamMapper.toDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamDto> getExamsByDurationRange(Double minDuration, Double maxDuration, int page, int size) {
        Objects.requireNonNull(minDuration, "Thời lượng tối thiểu không được để trống");
        Objects.requireNonNull(maxDuration, "Thời lượng tối đa không được để trống");
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Thời lượng tối thiểu phải nhỏ hơn hoặc bằng thời lượng tối đa");
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByDurationBetween(minDuration, maxDuration, pageable).map(ExamMapper::toDto);
    }

    /**
     * Counting and Statistics
     */

    @Transactional(readOnly = true)
    public long countExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        Objects.requireNonNull(scopeType, "Phạm vi đề thi không được để trống");
        return examRepository.countByExamScopeType(scopeType);
    }

    @Transactional(readOnly = true)
    public long countExamsByCourse(String courseId) {
        Objects.requireNonNull(courseId, "Mã khóa học không được để trống");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        return examRepository.countByCourse(course);
    }

    @Transactional(readOnly = true)
    public long countExamsByChapter(String chapterId) {
        Objects.requireNonNull(chapterId, "Mã chương không được để trống");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAPTER_NOT_FOUND_PREFIX + chapterId));
        return examRepository.countByChapter(chapter);
    }

    @Transactional(readOnly = true)
    public long countExamsByUnit(String unitId) {
        Objects.requireNonNull(unitId, "Mã bài học không được để trống");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        return examRepository.countByUnit(unit);
    }

    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        return examRepository.existsByTitleIgnoreCase(title);
    }

    /**
     * Create/Update Operations
     */

    public ExamDto createExam(CreateExamDto createExamDto) {
        Objects.requireNonNull(createExamDto, "Dữ liệu đề thi không được để trống");
        Objects.requireNonNull(createExamDto.title(), "Tiêu đề đề thi không được để trống");

        log.info("Đang tạo đề thi mới với tiêu đề: {}", createExamDto.title());

        /* Validate unique title */
        if (examRepository.existsByTitleIgnoreCase(createExamDto.title())) {
            throw new DuplicateResourceException(TITLE_EXISTS_PREFIX + createExamDto.title());
        }

        Exam exam = Exam.builder()
                .id(UUID.randomUUID().toString())
                .title(createExamDto.title())
                .description(createExamDto.description())
                .duration(createExamDto.duration())
                .examScopeType(createExamDto.examScopeType())
                .build();

        /* Add questions to exam */
        addQuestionsToExam(exam, createExamDto.questionIds());

        Exam savedExam = examRepository.save(exam);
        log.info("Tạo đề thi thành công với ID: {}", savedExam.getId());
        return ExamMapper.toDto(savedExam);
    }

    public ExamDto updateExam(String id, CreateExamDto updateDto) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(updateDto, "Dữ liệu cập nhật không được để trống");
        Objects.requireNonNull(updateDto.title(), "Tiêu đề đề thi không được để trống");

        log.info("Đang cập nhật đề thi với ID: {}", id);

        Exam exam = getExamEntityById(id);

        /* Check title uniqueness if changed */
        if (!exam.getTitle().equalsIgnoreCase(updateDto.title()) &&
                examRepository.existsByTitleIgnoreCase(updateDto.title())) {
            throw new DuplicateResourceException(TITLE_EXISTS_PREFIX + updateDto.title());
        }

        exam.setTitle(updateDto.title());
        exam.setDescription(updateDto.description());
        exam.setDuration(updateDto.duration());
        exam.setExamScopeType(updateDto.examScopeType());

        /* Update questions */
        exam.getQuestions().clear();
        addQuestionsToExam(exam, updateDto.questionIds());

        Exam updatedExam = examRepository.save(exam);
        log.info("Cập nhật đề thi thành công với ID: {}", updatedExam.getId());
        return ExamMapper.toDto(updatedExam);
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

    public ExamDto addQuestionToExam(String examId, String questionId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(questionId, "Question ID must not be null");

        log.info("Adding question ID: {} to exam ID: {}", questionId, examId);

        Exam exam = getExamEntityById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + questionId));

        if (!exam.getQuestions().contains(question)) {
            exam.getQuestions().add(question);
            exam = examRepository.save(exam);
        }
        return ExamMapper.toDto(exam);
    }

    public ExamDto removeQuestionFromExam(String examId, String questionId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(questionId, "Question ID must not be null");

        log.info("Removing question ID: {} from exam ID: {}", questionId, examId);

        Exam exam = getExamEntityById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + questionId));

        exam.getQuestions().remove(question);
        exam = examRepository.save(exam);
        return ExamMapper.toDto(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsWithoutQuestions() {
        List<Exam> exams = examRepository.findExamsWithoutQuestions();
        return ExamMapper.toDtoList(exams);
    }

    private Exam getExamEntityById(String id) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);

        Exam exam = examRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND_PREFIX + id));

        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            for (Question question : exam.getQuestions()) {
                question.getOptions().size();
            }
        }

        return exam;
    }

    private void addQuestionsToExam(Exam exam, List<String> questionIds) {
        if (questionIds != null && !questionIds.isEmpty()) {
            List<Question> questions = questionRepository.findAllById(questionIds);
            exam.getQuestions().addAll(questions);
        }
    }
}

