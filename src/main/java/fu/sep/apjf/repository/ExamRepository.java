package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;
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

    // Bỏ query gây lỗi MultipleBagFetchException
    // @Query("SELECT DISTINCT e FROM Exam e " +
    //        "LEFT JOIN FETCH e.questions q " +
    //        "LEFT JOIN FETCH q.options o " +
    //        "WHERE e.id = :id")
    // Optional<Exam> findByIdWithQuestionsAndOptionsOnly(@Param("id") String id);

    @Query("SELECT e FROM Exam e WHERE e.id = :id")
    Optional<Exam> findByIdOnly(@Param("id") String id);

    // Query riêng để fetch questions với options
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.options o " +
           "WHERE q IN (SELECT qu FROM Exam e JOIN e.questions qu WHERE e.id = :examId)")
    List<Question> findQuestionsByExamIdWithOptions(@Param("examId") String examId);
}
