package fu.sep.cms.repository;

import fu.sep.cms.entity.Slot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    @EntityGraph(attributePaths = {"materials", "materials.type"})
    Optional<Slot> findById(Long id);
}
