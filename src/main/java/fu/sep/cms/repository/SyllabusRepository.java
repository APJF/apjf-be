package fu.sep.cms.repository;

import fu.sep.cms.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Optional<Syllabus> findBName(String title);
}
