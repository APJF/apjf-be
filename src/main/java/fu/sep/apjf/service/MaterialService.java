package fu.sep.apjf.service;
import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final ApprovalRequestService approvalRequestService;
    private final MaterialMapper materialMapper;
    private final MinioService minioService; // Thêm MinioService injection

    private static final String UNIT_NOT_FOUND_PREFIX = "Không tìm thấy bài học với ID: ";
    private static final String MATERIAL_NOT_FOUND_PREFIX = "Không tìm thấy tài liệu với ID: ";

    /**
     * Tìm tất cả tài liệu dạng list
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDto> findAll() {
        return materialRepository.findAll().stream()
                .map(this::toDtoWithPresignedUrl)
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
                .map(this::toDtoWithPresignedUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDto findById(String id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));
        return toDtoWithPresignedUrl(material);
    }

    // Helper method để map Material entity sang DTO với presigned URL
    private MaterialResponseDto toDtoWithPresignedUrl(Material material) {
        MaterialResponseDto dto = materialMapper.toDto(material);

        String fileUrl = dto.fileUrl();
        if (fileUrl != null && !fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
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

    public MaterialResponseDto create(@Valid MaterialRequestDto dto, String unitId, Long staffId) {
        log.info("Nhân viên {} tạo tài liệu mới", staffId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));

        String materialId = dto.id() != null ? dto.id() : UUID.randomUUID().toString();

        Material material = materialMapper.toEntity(dto, unit);
        material.setId(materialId);

        Material savedMaterial = materialRepository.save(material);

        // Auto-create approval request for this new material
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                savedMaterial.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo tài liệu {} và yêu cầu phê duyệt thành công", savedMaterial.getId());
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

        // Auto-create approval request for this updated material
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                savedMaterial.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật tài liệu {} và yêu cầu phê duyệt thành công", savedMaterial.getId());
        return materialMapper.toDto(savedMaterial);
    }

}
