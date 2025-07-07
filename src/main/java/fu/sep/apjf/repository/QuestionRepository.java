package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByType(EnumClass.QuestionType type);

    @Query("SELECT q FROM Question q WHERE q.content LIKE %:keyword%")
    List<Question> findByContentContaining(@Param("keyword") String keyword);

    @Query("SELECT q FROM Question q JOIN q.exams e WHERE e.id = :examId")
    List<Question> findByExamId(@Param("examId") String examId);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.exams e WHERE e.id = :examId")
    long countByExamId(@Param("examId") String examId);
}
