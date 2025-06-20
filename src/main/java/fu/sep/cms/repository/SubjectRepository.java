package fu.sep.cms.repository;

import fu.sep.cms.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Page<Subject> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Subject> findByLevel(String level, Pageable pageable);
    Page<Subject> findByCreatorId(String creatorId, Pageable pageable);
    Page<Subject> findByLevelAndCreatorId(String level, String creatorId, Pageable pageable);
    Page<Subject> findByLevelAndTitleContainingIgnoreCase(String level, String title, Pageable pageable);
    Page<Subject> findByCreatorIdAndTitleContainingIgnoreCase(String creatorId, String title, Pageable pageable);
    Page<Subject> findByLevelAndCreatorIdAndTitleContainingIgnoreCase(
            String level, String creatorId, String title, Pageable pageable);
    @EntityGraph(attributePaths = {"chapters", "chapters.slots"})
    Optional<Subject> findWithChaptersAndSlotsById(Long id);
}
