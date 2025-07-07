package fu.sep.apjf.repository;

import fu.sep.apjf.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, String> {

    List<QuestionOption> findByQuestionId(String questionId);

    @Query("SELECT qo FROM QuestionOption qo WHERE qo.question.id = :questionId AND qo.isCorrect = true")
    List<QuestionOption> findCorrectOptionsByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT qo FROM QuestionOption qo WHERE qo.question.id = :questionId AND qo.isCorrect = false")
    List<QuestionOption> findIncorrectOptionsByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT COUNT(qo) FROM QuestionOption qo WHERE qo.question.id = :questionId")
    long countByQuestionId(@Param("questionId") String questionId);
}
