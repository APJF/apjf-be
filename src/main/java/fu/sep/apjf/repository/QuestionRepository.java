package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    // Thêm EntityGraph để eager load options
    @EntityGraph(attributePaths = {"options"})
    @Override
    List<Question> findAll();

    @EntityGraph(attributePaths = {"options"})
    @Override
    Optional<Question> findById(String id);

    @EntityGraph(attributePaths = {"options"})
    @Query("SELECT q FROM Question q JOIN q.exams e WHERE e = :exam")
    List<Question> findByExamsContaining(@Param("exam") Exam exam);

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find questions by type
     */
    List<Question> findByType(EnumClass.QuestionType type);

    /**
     * Find questions by scope
     */
    List<Question> findByScope(EnumClass.QuestionScope scope);

    // ========== TEXT SEARCH (Using naming convention) ==========

    /**
     * Find questions by content containing text (case insensitive)
     */
    List<Question> findByContentContainingIgnoreCase(String content);

    /**
     * Find questions by explanation containing text (case insensitive)
     */
    List<Question> findByExplanationContainingIgnoreCase(String explanation);

    /**
     * Find questions by correct answer containing text (case insensitive)
     */
    List<Question> findByCorrectAnswerContainingIgnoreCase(String correctAnswer);

    // ========== NULL/NOT NULL CHECKS (Using naming convention) ==========

    /**
     * Find questions that have correct answer (not null)
     */
    List<Question> findByCorrectAnswerIsNotNull();

    /**
     * Find questions that don't have correct answer (null)
     */
    List<Question> findByCorrectAnswerIsNull();

    /**
     * Find questions that have explanation
     */
    List<Question> findByExplanationIsNotNull();

    /**
     * Find questions that have file URL
     */
    List<Question> findByFileUrlIsNotNull();

    // ========== DATE-BASED QUERIES (Using naming convention) ==========

    /**
     * Find questions created after specific date
     */
    List<Question> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find questions created before specific date
     */
    List<Question> findByCreatedAtBefore(LocalDateTime date);

    /**
     * Find questions created within date range
     */
    List<Question> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ========== COMBINED CRITERIA (Using naming convention) ==========

    /**
     * Find questions by type and scope
     */
    List<Question> findByTypeAndScope(EnumClass.QuestionType type, EnumClass.QuestionScope scope);

    /**
     * Find questions by type that have correct answer
     */
    List<Question> findByTypeAndCorrectAnswerIsNotNull(EnumClass.QuestionType type);

    /**
     * Find questions by scope that have explanation
     */
    List<Question> findByScopeAndExplanationIsNotNull(EnumClass.QuestionScope scope);

    // ========== ORDERING QUERIES (Using naming convention) ==========

    /**
     * Find questions by type ordered by creation date (newest first)
     */
    List<Question> findByTypeOrderByCreatedAtDesc(EnumClass.QuestionType type);

    /**
     * Find questions by scope ordered by content
     */
    List<Question> findByScopeOrderByContentAsc(EnumClass.QuestionScope scope);

    /**
     * Find all questions ordered by creation date (newest first)
     */
    List<Question> findByOrderByCreatedAtDesc();

    // ========== COUNTING METHODS (Using naming convention) ==========

    /**
     * Count questions by type
     */
    long countByType(EnumClass.QuestionType type);

    /**
     * Count questions by scope
     */
    long countByScope(EnumClass.QuestionScope scope);

    /**
     * Count questions that have correct answer
     */
    long countByCorrectAnswerIsNotNull();

    /**
     * Count questions by type and scope
     */
    long countByTypeAndScope(EnumClass.QuestionType type, EnumClass.QuestionScope scope);

    // ========== EXISTENCE CHECKS (Using naming convention) ==========

    /**
     * Check if questions exist for specific type
     */
    boolean existsByType(EnumClass.QuestionType type);

    /**
     * Check if questions exist for specific scope
     */
    boolean existsByScope(EnumClass.QuestionScope scope);

    /**
     * Check if question exists by content (case insensitive)
     */
    boolean existsByContentIgnoreCase(String content);

    // ========== RELATIONSHIP-BASED QUERIES (Custom queries when necessary) ==========

    /**
     * Find questions associated with specific unit
     * Note: Uses Many-to-Many relationship
     */
    @Query("SELECT q FROM Question q JOIN q.units u WHERE u = :unit")
    List<Question> findByUnitsContaining(@Param("unit") Unit unit);

    /**
     * Count questions in specific exam
     */
    @Query("SELECT COUNT(q) FROM Question q JOIN q.exams e WHERE e = :exam")
    long countByExamsContaining(@Param("exam") Exam exam);

    /**
     * Find questions not yet assigned to any exam
     */
    @Query("SELECT q FROM Question q WHERE SIZE(q.exams) = 0")
    List<Question> findQuestionsWithoutExams();

    /**
     * Find questions not yet assigned to any unit
     */
    @Query("SELECT q FROM Question q WHERE SIZE(q.units) = 0")
    List<Question> findQuestionsWithoutUnits();

    /**
     * Find questions that can be used in multiple choice exams (have options)
     */
    @Query("SELECT q FROM Question q WHERE SIZE(q.options) > 0")
    List<Question> findQuestionsWithOptions();

    /**
     * Find questions without any options
     */
    @Query("SELECT q FROM Question q WHERE SIZE(q.options) = 0")
    List<Question> findQuestionsWithoutOptions();
}
