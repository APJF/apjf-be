package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {

    List<Exam> findByExamScopeType(EnumClass.ExamScopeType scopeType);

    @Query("SELECT e FROM Exam e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Exam> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    @Query("SELECT e FROM Exam e WHERE e.duration BETWEEN :minDuration AND :maxDuration")
    List<Exam> findByDurationBetween(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);

    @Query("SELECT e FROM Exam e JOIN e.questions q WHERE q.id = :questionId")
    List<Exam> findByQuestionId(@Param("questionId") String questionId);
}
