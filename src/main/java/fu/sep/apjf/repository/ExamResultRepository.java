package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, String> {

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find exam results by user
     */
    List<ExamResult> findByUser(User user);

    /**
     * Find exam results by exam
     */
    List<ExamResult> findByExam(Exam exam);

    /**
     * Find exam results by status
     */
    List<ExamResult> findByStatus(EnumClass.ExamStatus status);

    /**
     * Find exam result by user and exam
     */
    Optional<ExamResult> findByUserAndExam(User user, Exam exam);

    // ========== STATUS AND DATE QUERIES ==========

    /**
     * Find exam results where submitted date is null (in progress)
     */
    List<ExamResult> findBySubmittedAtIsNull();

    /**
     * Find exam results where submitted date is not null (completed)
     */
    List<ExamResult> findBySubmittedAtIsNotNull();

    /**
     * Find exam results submitted within date range
     */
    List<ExamResult> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find exam results started after specific date
     */
    List<ExamResult> findByStartedAtAfter(LocalDateTime date);

    /**
     * Find exam results submitted before specific date
     */
    List<ExamResult> findBySubmittedAtBefore(LocalDateTime date);

    // ========== COMBINED CRITERIA ==========

    /**
     * Find exam results by user and status
     */
    List<ExamResult> findByUserAndStatus(User user, EnumClass.ExamStatus status);

    /**
     * Find exam results by exam and status
     */
    List<ExamResult> findByExamAndStatus(Exam exam, EnumClass.ExamStatus status);

    /**
     * Find exam results by user where submitted date is null
     */
    List<ExamResult> findByUserAndSubmittedAtIsNull(User user);

    /**
     * Find exam results by exam where submitted date is null
     */
    List<ExamResult> findByExamAndSubmittedAtIsNull(Exam exam);

    // ========== SCORE-BASED QUERIES ==========

    /**
     * Find exam results with score greater than or equal to specific value
     */
    List<ExamResult> findByScoreGreaterThanEqual(Float minScore);

    /**
     * Find exam results with score less than specific value
     */
    List<ExamResult> findByScoreLessThan(Float maxScore);

    /**
     * Find exam results by user with score greater than or equal to specific value
     */
    List<ExamResult> findByUserAndScoreGreaterThanEqual(User user, Float minScore);

    // ========== ORDERING QUERIES ==========

    /**
     * Find exam results by user ordered by start date (newest first)
     */
    List<ExamResult> findByUserOrderByStartedAtDesc(User user);

    /**
     * Find exam results by exam ordered by score (highest first)
     */
    List<ExamResult> findByExamOrderByScoreDesc(Exam exam);

    /**
     * Find exam results by status ordered by submitted date
     */
    List<ExamResult> findByStatusOrderBySubmittedAtDesc(EnumClass.ExamStatus status);

    // ========== COUNTING METHODS ==========

    /**
     * Count exam results by user
     */
    long countByUser(User user);

    /**
     * Count exam results by exam
     */
    long countByExam(Exam exam);

    /**
     * Count exam results by status
     */
    long countByStatus(EnumClass.ExamStatus status);

    /**
     * Count exam results by user and status
     */
    long countByUserAndStatus(User user, EnumClass.ExamStatus status);

    /**
     * Count exam results by exam and status
     */
    long countByExamAndStatus(Exam exam, EnumClass.ExamStatus status);

    // ========== EXISTENCE CHECKS ==========

    /**
     * Check if exam result exists for user and exam
     */
    boolean existsByUserAndExam(User user, Exam exam);

    /**
     * Check if user has any completed exam results
     */
    boolean existsByUserAndSubmittedAtIsNotNull(User user);

    // ========== CUSTOM QUERIES (Only when necessary) ==========

    /**
     * Get average score by exam
     */
    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.exam = :exam AND er.score IS NOT NULL")
    Double getAverageScoreByExam(@Param("exam") Exam exam);

    /**
     * Find top performers by exam (limit requires custom query)
     */
    @Query("SELECT er FROM ExamResult er WHERE er.exam = :exam AND er.score IS NOT NULL ORDER BY er.score DESC LIMIT :limit")
    List<ExamResult> findTopPerformersByExam(@Param("exam") Exam exam, @Param("limit") int limit);

    /**
     * Get completion rate by exam
     */
    @Query("SELECT (COUNT(er) * 1.0 / (SELECT COUNT(total) FROM ExamResult total WHERE total.exam = :exam)) " +
           "FROM ExamResult er WHERE er.exam = :exam AND er.submittedAt IS NOT NULL")
    Double getCompletionRateByExam(@Param("exam") Exam exam);
}
