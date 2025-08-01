package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Question;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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

}
