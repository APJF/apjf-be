package fu.sep.apjf.repository;

import fu.sep.apjf.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    List<PostReport> findAll();

    Optional<PostReport> findById(Long id);

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
