package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {

    List<Exam> findByUnitId(String unitId);

    List<Exam> findByChapterId(String chapterId); // Nếu cần

    List<Exam> findByCourseId(String courseId); // Nếu cần

    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.questions WHERE e.id = :id")
    Optional<Exam> findByIdWithQuestions(@Param("id") String id);
}
