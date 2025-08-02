package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultDetailRepository extends JpaRepository<ExamResultDetail, Long> {
    List<ExamResultDetail> findByExamResultId(Long examResultId);

    List<ExamResultDetail> findByQuestionId(String questionId);

    @Query("SELECT erd FROM ExamResultDetail erd WHERE erd.examResult.id = :examResultId AND erd.question.id = :questionId")
    ExamResultDetail findByExamResultIdAndQuestionId(@Param("examResultId") Long examResultId, @Param("questionId") String questionId);

    @Query("SELECT COUNT(erd) FROM ExamResultDetail erd WHERE erd.examResult.id = :examResultId AND erd.isCorrect = true")
    int countCorrectAnswersByExamResultId(@Param("examResultId") Long examResultId);

    @Query("SELECT COUNT(erd) FROM ExamResultDetail erd WHERE erd.examResult.id = :examResultId")
    int countTotalAnswersByExamResultId(@Param("examResultId") Long examResultId);
}
