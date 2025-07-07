package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResultAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultAnswerRepository extends JpaRepository<ExamResultAnswer, String> {

    List<ExamResultAnswer> findByExamResultId(String examResultId);

    List<ExamResultAnswer> findByQuestionId(String questionId);

    @Query("SELECT era FROM ExamResultAnswer era WHERE era.examResult.id = :examResultId AND era.question.id = :questionId")
    ExamResultAnswer findByExamResultIdAndQuestionId(@Param("examResultId") String examResultId, @Param("questionId") String questionId);

    @Query("SELECT COUNT(era) FROM ExamResultAnswer era WHERE era.examResult.id = :examResultId AND era.isCorrect = true")
    long countCorrectAnswersByExamResultId(@Param("examResultId") String examResultId);

    @Query("SELECT COUNT(era) FROM ExamResultAnswer era WHERE era.examResult.id = :examResultId")
    long countTotalAnswersByExamResultId(@Param("examResultId") String examResultId);
}
