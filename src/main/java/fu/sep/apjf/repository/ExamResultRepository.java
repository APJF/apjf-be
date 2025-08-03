package fu.sep.apjf.repository;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByUser(User user);

    List<ExamResult> findByExam(Exam exam);

    List<ExamResult> findByStatus(EnumClass.ExamStatus status);

    Optional<ExamResult> findByUserAndExam(User user, Exam exam);

    Optional<ExamResult> findByUserAndExamAndSubmittedAtIsNull(User user, Exam exam);

    List<ExamResult> findBySubmittedAtIsNull();

    List<ExamResult> findBySubmittedAtIsNotNull();

    List<ExamResult> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ExamResult> findByStartedAtAfter(LocalDateTime date);

    List<ExamResult> findBySubmittedAtBefore(LocalDateTime date);

    List<ExamResult> findByUserAndStatus(User user, EnumClass.ExamStatus status);

    List<ExamResult> findByExamAndStatus(Exam exam, EnumClass.ExamStatus status);

    List<ExamResult> findByUserAndSubmittedAtIsNull(User user);

    List<ExamResult> findByExamAndSubmittedAtIsNull(Exam exam);

    List<ExamResult> findByScoreGreaterThanEqual(Float minScore);

    List<ExamResult> findByScoreLessThan(Float maxScore);

    long countByUserAndStatus(User user, EnumClass.ExamStatus status);

    boolean existsByUserAndExam(User user, Exam exam);

    @Query("SELECT AVG(e.score) FROM ExamResult e WHERE e.exam = :exam AND e.submittedAt IS NOT NULL")
    Double getAverageScoreByExam(Exam exam);

    List<ExamResult> findByUserAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(User user);

    Page<ExamResult> findByUserAndSubmittedAtIsNotNull(User user, Pageable pageable);

    List<ExamResult> findByUserAndStatusOrderBySubmittedAtDesc(User user, EnumClass.ExamStatus status);
}
