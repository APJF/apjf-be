package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    List<Material> findByUnit(Unit unit);
}
