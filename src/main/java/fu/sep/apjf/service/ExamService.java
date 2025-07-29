package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.DuplicateResourceException;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import java.util.*;

/**
 * @author hp
 */
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
    public List<ExamResponseDto> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getAllExams(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return examRepository.findAll(pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public ExamResponseDto getExamById(String id) {
        Objects.requireNonNull(id, EXAM_ID_NULL_MESSAGE);

        Exam exam = examRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException(EXAM_NOT_FOUND_PREFIX + id));

        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            for (Question question : exam.getQuestions()) {
                // Truy cập collection options để force Hibernate tải chúng
                if (!question.getOptions().isEmpty()) {
                    // Đã tải options thành công
                    log.debug("Loaded {} options for question {}", question.getOptions().size(), question.getId());
                }
            }
        }

        return ExamMapper.toResponseDto(exam);
    }

    /**
     * Finder Methods Using Updated Repository
     */

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByScopeType(EnumClass.ExamScopeType scopeType) {
        Objects.requireNonNull(scopeType, "Phạm vi đề thi không được để trống");
        List<Exam> exams = examRepository.findByExamScopeType(scopeType);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getExamsByScopeType(EnumClass.ExamScopeType scopeType, int page, int size) {
        Objects.requireNonNull(scopeType, "Phạm vi đề thi không được để trống");
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByExamScopeType(scopeType, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByCourse(String courseId) {
        Objects.requireNonNull(courseId, "Mã khóa học không được để trống");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        List<Exam> exams = examRepository.findByCourse(course);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getExamsByCourse(String courseId, int page, int size) {
        Objects.requireNonNull(courseId, "Mã khóa học không được để trống");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByCourse(course, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByChapter(String chapterId) {
        Objects.requireNonNull(chapterId, "Mã chương không được để trống");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAPTER_NOT_FOUND_PREFIX + chapterId));
        List<Exam> exams = examRepository.findByChapter(chapter);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getExamsByChapter(String chapterId, int page, int size) {
        Objects.requireNonNull(chapterId, "Mã chương không được để trống");
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAPTER_NOT_FOUND_PREFIX + chapterId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByChapter(chapter, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByUnit(String unitId) {
        Objects.requireNonNull(unitId, "Mã bài học không được để trống");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        List<Exam> exams = examRepository.findByUnit(unit);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getExamsByUnit(String unitId, int page, int size) {
        Objects.requireNonNull(unitId, "Mã bài học không được để trống");
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByUnit(unit, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> searchExamsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Exam> exams = examRepository.findByTitleContainingIgnoreCase(title);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> searchExamsByTitle(String title, int page, int size) {
        if (title == null || title.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByTitleContainingIgnoreCase(title, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> searchExams(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Exam> exams = examRepository.searchByTitleOrDescription(keyword);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> searchExams(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.searchByTitleOrDescription(keyword, pageable).map(ExamMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByDurationRange(Double minDuration, Double maxDuration) {
        Objects.requireNonNull(minDuration, "Thời lượng tối thiểu không được để trống");
        Objects.requireNonNull(maxDuration, "Thời lượng tối đa không được để trống");
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Thời lượng tối thiểu phải nhỏ hơn hoặc bằng thời lượng tối đa");
        }
        List<Exam> exams = examRepository.findByDurationBetween(minDuration, maxDuration);
        return ExamMapper.toResponseDtoList(exams);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getExamsByDurationRange(Double minDuration, Double maxDuration, int page, int size) {
        Objects.requireNonNull(minDuration, "Thời lượng tối thiểu không được để trống");
        Objects.requireNonNull(maxDuration, "Thời lượng tối đa không được để trống");
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Thời lượng tối thiểu phải nhỏ hơn hoặc bằng thời lượng tối đa");
        }
        Pageable pageable = PageRequest.of(page, size);
        return examRepository.findByDurationBetween(minDuration, maxDuration, pageable).map(ExamMapper::toResponseDto);
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

    public ExamResponseDto createExam(ExamRequestDto createExamDto) {
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
        if (createExamDto.questionIds() != null) {
            Set<String> questionIdSet = new HashSet<>(createExamDto.questionIds());
            addQuestionsToExam(exam, questionIdSet);
        }

        Exam savedExam = examRepository.save(exam);
        log.info("Tạo đề thi thành công với ID: {}", savedExam.getId());
        return ExamMapper.toResponseDto(savedExam);
    }

    public ExamResponseDto updateExam(String id, ExamRequestDto updateDto) {
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
        if (updateDto.questionIds() != null) {
            Set<String> questionIdSet = new HashSet<>(updateDto.questionIds());
            addQuestionsToExam(exam, questionIdSet);
        }

        Exam updatedExam = examRepository.save(exam);
        log.info("Cập nhật đề thi thành công với ID: {}", updatedExam.getId());
        return ExamMapper.toResponseDto(updatedExam);
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

    public ExamResponseDto addQuestionToExam(String examId, String questionId) {
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
        return ExamMapper.toResponseDto(exam);
    }

    public ExamResponseDto removeQuestionFromExam(String examId, String questionId) {
        Objects.requireNonNull(examId, EXAM_ID_NULL_MESSAGE);
        Objects.requireNonNull(questionId, "Question ID must not be null");

        log.info("Removing question ID: {} from exam ID: {}", questionId, examId);

        Exam exam = getExamEntityById(examId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + questionId));

        exam.getQuestions().remove(question);
        exam = examRepository.save(exam);
        return ExamMapper.toResponseDto(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsWithoutQuestions() {
        List<Exam> exams = examRepository.findExamsWithoutQuestions();
        return ExamMapper.toResponseDtoList(exams);
    }

    // Method mới cho ExamService
    public ExamResponseDto getExamDetail(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi: " + examId));
        return ExamMapper.toResponseDto(exam);
    }

    /**
     * Lấy danh sách các bài thi mà người dùng có thể làm
     */
    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getAvailableExams(Long userId, String courseId, int page, int size, String sort, String direction) {
        // Xác định hướng sắp xếp
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Lọc theo courseId nếu được cung cấp
        List<Exam> availableExams;
        if (courseId != null && !courseId.trim().isEmpty()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_PREFIX + courseId));
            availableExams = examRepository.findByCourse(course);
        } else {
            availableExams = examRepository.findAll();
        }

        // Chuyển danh sách thành Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), availableExams.size());

        if (start > availableExams.size()) {
            return Page.empty();
        }

        List<Exam> pageContent = availableExams.subList(start, end);
        Page<Exam> examPage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, availableExams.size());

        // Chuyển đổi sang DTO
        return examPage.map(ExamMapper::toResponseDto);
    }

    /**
     * Mở rộng phương thức getAllExams để hỗ trợ thêm các tham số lọc
     */
    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getAllExams(int page, int size, String sort, String direction,
                                      String status, String courseId, String searchTerm, String difficultyLevel) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Tạo đối tượng Specification để lọc động
        Specification<Exam> spec = (root, query, criteriaBuilder) -> null;

        // Lọc theo trạng thái nếu có
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), EnumClass.Status.valueOf(status)));
        }

        // Lọc theo khóa học nếu có
        if (courseId != null && !courseId.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Exam, Course> courseJoin = root.join("course");
                return criteriaBuilder.equal(courseJoin.get("id"), courseId);
            });
        }

        // Tìm kiếm theo từ khóa
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
                    ));
        }

        // Lọc theo độ khó (nếu có thuộc tính này, nếu không thì bỏ qua điều kiện này)
        if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
            try {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("difficultyLevel"), difficultyLevel));
            } catch (Exception e) {
                // Bỏ qua nếu không có trường difficultyLevel
                log.warn("Không tìm thấy thuộc tính difficultyLevel trong entity Exam");
            }
        }

        // Thực hiện truy vấn
        Page<Exam> exams = examRepository.findAll(spec, pageable);
        return exams.map(ExamMapper::toResponseDto);
    }

    /**
     * Lấy đối tượng Exam theo ID
     */
    private Exam getExamEntityById(String id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài thi với ID: " + id));
    }

    /**
     * Thêm danh sách câu hỏi vào bài thi
     */
    private void addQuestionsToExam(Exam exam, Set<String> questionIds) {
        if (questionIds != null && !questionIds.isEmpty()) {
            // Xóa tất cả câu hỏi hiện có
            exam.getQuestions().clear();

            // Thêm các câu hỏi mới
            for (String qId : questionIds) {
                Question question = questionRepository.findById(qId)
                        .orElseThrow(() -> new ResourceNotFoundException(QUESTION_NOT_FOUND_PREFIX + qId));
                exam.getQuestions().add(question);
            }
        }
    }
}
