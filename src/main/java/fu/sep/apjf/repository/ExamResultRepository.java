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
SELECT DISTINCT er
FROM ExamResult er
JOIN FETCH er.exam e
LEFT JOIN FETCH er.details d
LEFT JOIN FETCH d.question q
LEFT JOIN FETCH d.selectedOption o
WHERE er.id = :id
""")
    Optional<ExamResult> findByIdWithDetails(@Param("id") Long id);

    @Query("""
SELECT er
FROM ExamResult er
JOIN FETCH er.exam e
WHERE er.user.id = :userId
""")
    List<ExamResult> findByUserIdWithExam(@Param("userId") Long userId);

    List<ExamResult> findByUser_IdAndExam_Id(Long userId, String examId);
    List<ExamResult> findByExam_Id(String examId);
    Optional<ExamResult> findTopByUser_IdAndExam_IdOrderBySubmittedAtDesc(Long userId, String examId);

    @Query("""
    SELECT er FROM ExamResult er
    WHERE er.user.id = :userId AND er.exam.id = :examId
      AND er.status = 'IN_PROGRESS'
    ORDER BY er.startedAt DESC
    LIMIT 1
""")
    Optional<ExamResult> findLatestInProgress(@Param("userId") Long userId,
                                              @Param("examId") String examId);

    @Query("""
    SELECT er FROM ExamResult er
    WHERE er.user.id = :userId AND er.exam.id = :examId AND er.status = :status
    ORDER BY er.startedAt DESC
    """)
    ExamResult findByUserIdAndExamIdAndStatus(@Param("userId") Long userId,
                                             @Param("examId") String examId,
                                             @Param("status") fu.sep.apjf.entity.EnumClass.ExamStatus status);

    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.questions WHERE e.id = :examId")
    Optional<Exam> findByIdWithQuestions(@Param("examId") String examId);

}