package fu.sep.apjf.repository;

import fu.sep.apjf.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    List<CommentReport> findAll();

    Optional<CommentReport> findById(Long id);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
