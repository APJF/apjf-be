package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String>, JpaSpecificationExecutor<Exam> {
    @NotNull
    @EntityGraph(attributePaths = {"questions"})
    @Override
    List<Exam> findAll();

    @NotNull
    @EntityGraph(attributePaths = {"questions"})
    @Override
    Optional<Exam> findById(@NotNull String id);
}
