package fu.sep.apjf.service;
import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.EnumClass;
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

    private static final String UNIT_NOT_FOUND_PREFIX = "Không tìm thấy bài học với ID: ";
    private static final String MATERIAL_NOT_FOUND_PREFIX = "Không tìm thấy tài liệu với ID: ";

    /**
     * Tìm tất cả tài liệu dạng list
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDto> findAll() {
        return materialRepository.findAll().stream()
                .map(MaterialMapper::toResponseDto)
                .toList();
    }

    /**
     * Tìm tài liệu theo đơn vị học tập
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDto> findByUnitId(String unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));

        return materialRepository.findByUnit(unit).stream()
                .map(MaterialMapper::toResponseDto)
                .toList();
    }

    /**
     * Tìm tài liệu theo ID
     */
    @Transactional(readOnly = true)
    public MaterialResponseDto findById(String id) {
        return MaterialMapper.toResponseDto(materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id)));
    }

    /**
     * Tạo tài liệu mới
     */
    public MaterialResponseDto create(@Valid MaterialRequestDto dto, String unitId, Long staffId) {
        log.info("Nhân viên {} tạo tài liệu mới", staffId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));

        String materialId = dto.id() != null ? dto.id() : UUID.randomUUID().toString();

        Material material = Material.builder()
                .id(materialId)
                .description(dto.description())
                .fileUrl(dto.fileUrl())
                .type(dto.type())
                .unit(unit)
                .build();

        Material savedMaterial = materialRepository.save(material);

        // Auto-create approval request for this new material
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                savedMaterial.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo tài liệu {} và yêu cầu phê duyệt thành công", savedMaterial.getId());
        return MaterialMapper.toResponseDto(savedMaterial);
    }

    /**
     * Cập nhật tài liệu
     */
    public MaterialResponseDto update(String id, @Valid MaterialRequestDto dto, String unitId, Long staffId) {
        log.info("Nhân viên {} cập nhật tài liệu {}", staffId, id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));

        // Cập nhật thông tin
        material.setDescription(dto.description());
        material.setFileUrl(dto.fileUrl());
        material.setType(dto.type());

        // Cập nhật unit nếu có thay đổi
        if (unitId != null && !material.getUnit().getId().equals(unitId)) {
            Unit newUnit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new ResourceNotFoundException(UNIT_NOT_FOUND_PREFIX + unitId));
            material.setUnit(newUnit);
        }

        Material updatedMaterial = materialRepository.save(material);

        // Auto-create approval request for this updated material
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                updatedMaterial.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật tài liệu {} và yêu cầu phê duyệt thành công", updatedMaterial.getId());
        return MaterialMapper.toResponseDto(updatedMaterial);
    }

    /**
     * Xóa tài liệu
     */
    public void delete(String id) {
        log.info("Xóa tài liệu với ID: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MATERIAL_NOT_FOUND_PREFIX + id));

        materialRepository.delete(material);

        log.info("Xóa tài liệu {} thành công", id);
    }
}
