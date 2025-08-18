package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultDetailRepository extends JpaRepository<ExamResultDetail, Long> {
    List<ExamResultDetail> findByExamResultId(Long examResultId);

    @Query("""
        SELECT d
        FROM ExamResultDetail d
        JOIN FETCH d.question q
        LEFT JOIN FETCH q.options
        WHERE d.examResult.id = :examResultId
    """)
    List<ExamResultDetail> findByExamResultIdWithOptions(@Param("examResultId") Long examResultId);
}
