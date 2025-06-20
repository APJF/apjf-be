package fu.sep.cms.repository;

import fu.sep.cms.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Page<Chapter> findBySubjectId(Long subjectId, Pageable pageable);
    Page<Chapter> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Chapter> findBySubjectIdAndTitleContainingIgnoreCase(Long subjectId, String keyword, Pageable pageable);
}
