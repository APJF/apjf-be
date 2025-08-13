package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultDetailRepository extends JpaRepository<ExamResultDetail, Long> {
    List<ExamResultDetail> findByExamResultId(Long examResultId);
}
