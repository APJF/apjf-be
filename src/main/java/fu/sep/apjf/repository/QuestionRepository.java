package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByUnits_Id(String unitId);

    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options LEFT JOIN FETCH q.units JOIN q.exams e WHERE e.id = :examId")
    List<Question> findByExamId(@Param("examId") String examId);

    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options LEFT JOIN FETCH q.units")
    List<Question> findAllWithOptionsAndUnits();

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options LEFT JOIN FETCH q.units WHERE q.id = :id")
    Question findByIdWithOptionsAndUnits(@Param("id") String id);

    @EntityGraph(attributePaths = {"units"})
    @Query("""
    SELECT q FROM Question q
    LEFT JOIN q.units u
    WHERE (:questionId IS NULL OR q.id LIKE %:questionId%)
      AND (:unitId IS NULL OR u.id = :unitId)
""")
    Page<Question> searchQuestionsWithoutOptions(
            @Param("questionId") String questionId,
            @Param("unitId") String unitId,
            Pageable pageable
    );

}