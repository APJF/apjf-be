package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.MaterialMapper;
import fu.sep.apjf.repository.MaterialRepository;
import fu.sep.apjf.repository.UnitRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaterialService {

    private static final String UNIT_NOT_FOUND_PREFIX = "Không tìm thấy bài học với ID: ";
    private static final String MATERIAL_NOT_FOUND_PREFIX = "Không tìm thấy tài liệu với ID: ";
    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final MaterialMapper materialMapper;
    private final MinioService minioService; // Thêm MinioService injection

    /**
     * Tìm tất cả tài liệu dạng list
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDto> findAll() {
        return materialRepository.findAll().stream()
                .map(material -> {
                    MaterialResponseDto dto = materialMapper.toDto(material);
                    return convertFileUrl(dto);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> findByUnitId(String unitId) {
        List<Material> materials = materialRepository.findByUnitId(unitId);

        // Lazy validation: chỉ check unit existence nếu list rỗng để tối ưu queries
        if (materials.isEmpty() && !unitRepository.existsById(unitId)) {
            throw new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId);
        }

        return materials.stream()
                .map(material -> {
                    MaterialResponseDto dto = materialMapper.toDto(material);
                    return convertFileUrl(dto);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDto findById(String id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));
        MaterialResponseDto dto = materialMapper.toDto(material);
        return convertFileUrl(dto);
    }

    // Helper method để convert file URL
    private MaterialResponseDto convertFileUrl(MaterialResponseDto dto) {
        String fileUrl = dto.fileUrl();
        if (fileUrl != null && !fileUrl.trim().isEmpty() &&
                !fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
            try {
                fileUrl = minioService.getDocumentUrl(fileUrl);
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for document {}: {}", fileUrl, e.getMessage());
                // Giữ nguyên object name nếu có lỗi
            }
        }

        return new MaterialResponseDto(
                dto.id(),
                fileUrl,
                dto.type(),
                dto.script(),
                dto.translation()
        );
    }

    public MaterialResponseDto create(@Valid MaterialRequestDto dto, Long staffId) {
        log.info("Nhân viên {} tạo tài liệu mới", staffId);

        Unit unit = unitRepository.findById(dto.unitId())
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + dto.unitId()));

        String materialId = dto.id();

        Material material = materialMapper.toEntity(dto, unit);
        material.setId(materialId);

        Material savedMaterial = materialRepository.save(material);

        log.info("Tạo tài liệu {} thành công", savedMaterial.getId());
        return materialMapper.toDto(savedMaterial);
    }

    /**
     * Cập nhật tài liệu
     */
    public MaterialResponseDto update(String id, @Valid MaterialRequestDto dto, String unitId, Long staffId) {
        log.info("Nhân viên {} cập nhật tài liệu {}", staffId, id);

        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));

        // Xác định unit để sử dụng
        Unit unit = existingMaterial.getUnit(); // Giữ unit hiện tại làm mặc định
        if (unitId != null && !existingMaterial.getUnit().getId().equals(unitId)) {
            unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
        }

        // Sử dụng mapper để tạo material mới với thông tin cập nhật
        Material updatedMaterial = materialMapper.toEntity(dto, unit);
        updatedMaterial.setId(id); // Giữ nguyên ID

        Material savedMaterial = materialRepository.save(updatedMaterial);

        log.info("Cập nhật tài liệu {} thành công", savedMaterial.getId());
        return materialMapper.toDto(savedMaterial);
    }

    @Transactional
    public void delete(String id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));
        materialRepository.delete(material);
    }

}
