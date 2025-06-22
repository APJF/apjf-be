package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Material;
import fu.sep.cms.repository.MaterialRepository;
import fu.sep.cms.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    public Material create(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public Material update(Long id, Material updated) {
        Material existing = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setFileUrl(updated.getFileUrl());
        existing.setType(updated.getType());
        existing.setUploaderId(updated.getUploaderId());
        existing.setSlot(updated.getSlot());
        existing.setUpdatedAt(updated.getUpdatedAt());

        return materialRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found");
        }
        materialRepository.deleteById(id);
    }

    @Override
    public List<Material> getBySlotId(Long slotId) {
        return materialRepository.findBySlotId(slotId);
    }

    @Override
    public Material getById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }
}
