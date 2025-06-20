package fu.sep.cms.repository;

import fu.sep.cms.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    // Bạn có thể bổ sung findByChapterId(...) nếu cần
}
