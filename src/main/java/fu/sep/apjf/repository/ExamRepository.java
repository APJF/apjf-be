package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {

    // Thêm phương thức để lấy exam và eager load questions cùng với options của chúng
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions q LEFT JOIN FETCH q.options WHERE e.id = :id")
    Optional<Exam> findByIdWithQuestionsAndOptions(@Param("id") String id);

    // Thêm phương thức để lấy exam với eager load questions
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions WHERE e.id = :id")
    Optional<Exam> findByIdWithQuestions(@Param("id") String id);

    // Thêm EntityGraph để eager load questions
    @EntityGraph(attributePaths = {"questions"})
    @Override
    List<Exam> findAll();

    @EntityGraph(attributePaths = {"questions"})
    @Override
    Optional<Exam> findById(String id);

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find exams by exam scope type
     */
    List<Exam> findByExamScopeType(EnumClass.ExamScopeType scopeType);

    /**
     * Find exams by course
     */
    List<Exam> findByCourse(Course course);

    /**
     * Find exams by chapter
     */
    List<Exam> findByChapter(Chapter chapter);

    /**
     * Find exams by unit
     */
    List<Exam> findByUnit(Unit unit);

    // ========== TEXT SEARCH (Using naming convention) ==========

    /**
     * Find exams by title containing text (case insensitive)
     */
    List<Exam> findByTitleContainingIgnoreCase(String title);

    /**
     * Find exams by description containing text (case insensitive)
     */
    List<Exam> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find exams by title starting with text
     */
    List<Exam> findByTitleStartingWithIgnoreCase(String titlePrefix);

    // ========== DURATION-BASED QUERIES (Using naming convention) ==========

    /**
     * Find exams with duration greater than or equal to specific value
     */
    List<Exam> findByDurationGreaterThanEqual(Double minDuration);

    /**
     * Find exams with duration less than or equal to specific value
     */
    List<Exam> findByDurationLessThanEqual(Double maxDuration);

    /**
     * Find exams with duration between two values
     */
    List<Exam> findByDurationBetween(Double minDuration, Double maxDuration);

    // ========== DATE-BASED QUERIES (Using naming convention) ==========

    /**
     * Find exams created after specific date
     */
    List<Exam> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find exams created before specific date
     */
    List<Exam> findByCreatedAtBefore(LocalDateTime date);

    /**
     * Find exams created within date range
     */
    List<Exam> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ========== COMBINED CRITERIA (Using naming convention) ==========

    /**
     * Find exams by scope type and course
     */
    List<Exam> findByExamScopeTypeAndCourse(EnumClass.ExamScopeType scopeType, Course course);

    /**
     * Find exams by scope type and chapter
     */
    List<Exam> findByExamScopeTypeAndChapter(EnumClass.ExamScopeType scopeType, Chapter chapter);

    /**
     * Find exams by scope type and unit
     */
    List<Exam> findByExamScopeTypeAndUnit(EnumClass.ExamScopeType scopeType, Unit unit);

    // ========== ORDERING QUERIES (Using naming convention) ==========

    /**
     * Find exams ordered by creation date (newest first)
     */
    List<Exam> findByOrderByCreatedAtDesc();

    /**
     * Find exams by scope type ordered by title
     */
    List<Exam> findByExamScopeTypeOrderByTitleAsc(EnumClass.ExamScopeType scopeType);

    /**
     * Find exams by course ordered by duration
     */
    List<Exam> findByCourseOrderByDurationAsc(Course course);

    /**
     * Find exams by chapter ordered by creation date
     */
    List<Exam> findByChapterOrderByCreatedAtDesc(Chapter chapter);

    // ========== COUNTING METHODS (Using naming convention) ==========

    /**
     * Count exams by scope type
     */
    long countByExamScopeType(EnumClass.ExamScopeType scopeType);

    /**
     * Count exams by course
     */
    long countByCourse(Course course);

    /**
     * Count exams by chapter
     */
    long countByChapter(Chapter chapter);

    /**
     * Count exams by unit
     */
    long countByUnit(Unit unit);

    // ========== EXISTENCE CHECKS (Using naming convention) ==========

    /**
     * Check if exam exists by title (case insensitive)
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Check if exams exist for specific course
     */
    boolean existsByCourse(Course course);

    /**
     * Check if exams exist for specific chapter
     */
    boolean existsByChapter(Chapter chapter);

    /**
     * Check if exams exist for specific unit
     */
    boolean existsByUnit(Unit unit);

    // ========== COMPLEX QUERIES (Custom queries only when necessary) ==========

    /**
     * Find exams containing specific question
     * Note: Requires custom query due to Many-to-Many relationship navigation
     */
    @Query("SELECT e FROM Exam e JOIN e.questions q WHERE q = :question")
    List<Exam> findByQuestionsContaining(@Param("question") Question question);

    /**
     * Search exams by title or description containing keyword
     * Note: Could use naming convention but custom query is more readable for OR condition
     */
    @Query("SELECT e FROM Exam e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Exam> searchByTitleOrDescription(@Param("keyword") String keyword);

    /**
     * Find exams with question count greater than specified value
     * Note: Requires custom query for aggregate function
     */
    @Query("SELECT e FROM Exam e WHERE SIZE(e.questions) > :minQuestionCount")
    List<Exam> findByQuestionCountGreaterThan(@Param("minQuestionCount") int minQuestionCount);

    /**
     * Find exams with no questions assigned
     * Note: Could use naming convention but custom query is more explicit
     */
    @Query("SELECT e FROM Exam e WHERE SIZE(e.questions) = 0")
    List<Exam> findExamsWithoutQuestions();
}
