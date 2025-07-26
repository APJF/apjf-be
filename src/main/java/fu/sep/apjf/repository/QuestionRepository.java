package fu.sep.apjf.repository;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.Unit;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String>, JpaSpecificationExecutor<Question> {
    @NotNull
    @EntityGraph(attributePaths = {"options"})
    @Override
    List<Question> findAll();

    @NotNull
    @EntityGraph(attributePaths = {"options"})
    @Override
    Optional<Question> findById(@NotNull String id);

    @EntityGraph(attributePaths = {"options"})
    @Query("SELECT q FROM Question q JOIN q.exams e WHERE e = :exam")
    List<Question> findByExamsContaining(@Param("exam") Exam exam);

    List<Question> findByType(EnumClass.QuestionType type);

    List<Question> findByScope(EnumClass.QuestionScope scope);

    List<Question> findByContentContainingIgnoreCase(String content);

    List<Question> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Question> findByTypeAndScope(EnumClass.QuestionType type, EnumClass.QuestionScope scope);

    List<Question> findByOrderByCreatedAtDesc();

    long countByType(EnumClass.QuestionType type);

    long countByScope(EnumClass.QuestionScope scope);

    boolean existsByContentIgnoreCase(String content);

    @Query("SELECT q FROM Question q JOIN q.units u WHERE u = :unit")
    List<Question> findByUnitsContaining(@Param("unit") Unit unit);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.exams e WHERE e = :exam")
    long countByExamsContaining(@Param("exam") Exam exam);

    @Query("SELECT q FROM Question q WHERE SIZE(q.options) = 0")
    List<Question> findQuestionsWithoutOptions();
}
