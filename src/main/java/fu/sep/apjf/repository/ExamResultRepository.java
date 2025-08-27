package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    @Query("""
        SELECT er
        FROM ExamResult er
        JOIN FETCH er.exam e
        WHERE er.user.id = :userId
    """)
    List<ExamResult> findByUserIdWithExam(@Param("userId") Long userId);


    @Query("""
    SELECT er FROM ExamResult er
    WHERE er.user.id = :userId AND er.exam.id = :examId AND er.status = :status
    ORDER BY er.startedAt DESC
    """)
    ExamResult findByUserIdAndExamIdAndStatus(@Param("userId") Long userId,
                                             @Param("examId") String examId,
                                             @Param("status") fu.sep.apjf.entity.EnumClass.ExamStatus status);

    Optional<ExamResult> findByUserIdAndIdExamId(Long userId, String examId);

}