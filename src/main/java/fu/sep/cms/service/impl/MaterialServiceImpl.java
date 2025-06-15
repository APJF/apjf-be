package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Material;
import fu.sep.cms.repository.MaterialRepository;
import fu.sep.cms.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public Material updateMaterial(Long id, Material material) {
        Optional<Material> existing = materialRepository.findById(id);
        if (existing.isPresent()) {
            Material old = existing.get();
            old.setTitle(material.getTitle());
            old.setDescription(material.getDescription());
            old.setFileUrl(material.getFileUrl());
            old.setType(material.getType());
            old.setUploaderId(material.getUploaderId());
            old.setStatus(material.getStatus());
            old.setUpdatedAt(material.getUpdatedAt());
            old.setSubject(material.getSubject());
            return materialRepository.save(old);
        }
        throw new RuntimeException("Material not found with id: " + id);
    }

    @Override
    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }

    @Override
    public Page<Material> getAllMaterials(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return materialRepository.findAll(pageable);
    }

    @Override
    public Page<Material> searchMaterials(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return materialRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }
}
