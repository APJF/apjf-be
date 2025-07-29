package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, String> {
    List<Option> findByQuestionId(String questionId);

    @Query("SELECT qo FROM Option qo WHERE qo.question.id = :questionId AND qo.isCorrect = true")
    List<Option> findCorrectOptionsByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT qo FROM Option qo WHERE qo.question.id = :questionId AND qo.isCorrect = false")
    List<Option> findIncorrectOptionsByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT COUNT(qo) FROM Option qo WHERE qo.question.id = :questionId")
    long countByQuestionId(@Param("questionId") String questionId);
}
