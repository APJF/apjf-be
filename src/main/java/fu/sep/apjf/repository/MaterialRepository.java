package fu.sep.apjf.repository;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {

    List<Material> findByUnit(Unit unit);

    Page<Material> findByUnit(Unit unit, Pageable pageable);

    List<Material> findByType(EnumClass.MaterialType type);

    Page<Material> findByType(EnumClass.MaterialType type, Pageable pageable);

    List<Material> findByUnitAndType(Unit unit, EnumClass.MaterialType type);

    Page<Material> findByUnitAndType(Unit unit, EnumClass.MaterialType type, Pageable pageable);

    List<Material> findByDescriptionContainingIgnoreCase(String keyword);

    Page<Material> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
}
