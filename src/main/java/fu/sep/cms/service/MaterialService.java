package fu.sep.cms.service;

import fu.sep.cms.entity.Material;

import java.util.List;

public interface MaterialService {
    Material create(Material material);
    Material update(Long id, Material material);
    void delete(Long id);
    List<Material> getBySlotId(Long slotId);
    Material getById(Long id);
}
