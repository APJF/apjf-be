package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByUserId(Long userId);
    List<ExamResult> findByUser_IdAndExam_Id(Long userId, String examId);
    List<ExamResult> findByExam_Id(String examId);
    Optional<ExamResult> findTopByUser_IdAndExam_IdOrderBySubmittedAtDesc(Long userId, String examId);
}

