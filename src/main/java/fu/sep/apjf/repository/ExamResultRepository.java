package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, String> {

    List<ExamResult> findByUserId(String userId);

    List<ExamResult> findByExamId(String examId);

    List<ExamResult> findByStatus(EnumClass.ExamStatus status);

    @Query("SELECT er FROM ExamResult er WHERE er.userId = :userId AND er.exam.id = :examId")
    Optional<ExamResult> findByUserIdAndExamId(@Param("userId") String userId, @Param("examId") String examId);

    @Query("SELECT er FROM ExamResult er WHERE er.submittedAt IS NULL")
    List<ExamResult> findInProgressExams();

    @Query("SELECT er FROM ExamResult er WHERE er.submittedAt BETWEEN :startDate AND :endDate")
    List<ExamResult> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.exam.id = :examId AND er.score IS NOT NULL")
    Double getAverageScoreByExamId(@Param("examId") String examId);
}
