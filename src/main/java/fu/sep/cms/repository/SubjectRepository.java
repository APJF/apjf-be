package fu.sep.cms.repository;

import fu.sep.cms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByTitleContainingIgnoreCase(String keyword);
}
