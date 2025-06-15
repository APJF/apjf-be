package fu.sep.cms.repository;

import fu.sep.cms.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Page<Material> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
