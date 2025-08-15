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

    @Query("SELECT o FROM Option o WHERE o.question.id IN :questionIds")
    List<Option> findByQuestionIds(@Param("questionIds") List<String> questionIds);


}
