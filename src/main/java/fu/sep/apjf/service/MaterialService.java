package fu.sep.apjf.service;

import fu.sep.apjf.dto.MaterialDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final ApprovalRequestService approvalRequestService;

    @Transactional(readOnly = true)
    public List<MaterialDto> findAll() {
        return materialRepository.findAll().stream()
                .map(MaterialMapper::toDto)
               .toList();
    }

    @Transactional(readOnly = true)
    public Page<MaterialDto> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return materialRepository.findAll(pageable)
                .map(MaterialMapper::toDto);
    }

    @Transactional(readOnly = true)
    public MaterialDto findById(String id) {
        return MaterialMapper.toDto(materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu với ID: " + id)));
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> findByUnit(String unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + unitId));
        return materialRepository.findByUnit(unit).stream()
                .map(MaterialMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MaterialDto> findByUnit(String unitId, int page, int size) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + unitId));
        Pageable pageable = PageRequest.of(page, size);
        return materialRepository.findByUnit(unit, pageable)
                .map(MaterialMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> findByType(EnumClass.MaterialType type) {
        return materialRepository.findByType(type).stream()
                .map(MaterialMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MaterialDto> findByType(EnumClass.MaterialType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return materialRepository.findByType(type, pageable)
                .map(MaterialMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> findByUnitAndType(String unitId, EnumClass.MaterialType type) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + unitId));
        return materialRepository.findByUnitAndType(unit, type).stream()
                .map(MaterialMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MaterialDto> findByUnitAndType(String unitId, EnumClass.MaterialType type, int page, int size) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + unitId));
        Pageable pageable = PageRequest.of(page, size);
        return materialRepository.findByUnitAndType(unit, type, pageable)
                .map(MaterialMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> searchByDescription(String keyword) {
        return materialRepository.findByDescriptionContainingIgnoreCase(keyword).stream()
                .map(MaterialMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MaterialDto> searchByDescription(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return materialRepository.findByDescriptionContainingIgnoreCase(keyword, pageable)
                .map(MaterialMapper::toDto);
    }

    public MaterialDto create(@Valid MaterialDto dto, String staffId) {
        log.info("Nhân viên {} tạo tài liệu mới với ID: {}", staffId, dto.id());

        Unit unit = unitRepository.findById(dto.unitId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + dto.unitId()));

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
        return MaterialMapper.toDto(savedMaterial);
    }

    public MaterialDto update(String id, @Valid MaterialDto dto, String staffId) {
        log.info("Nhân viên {} cập nhật tài liệu với ID: {}", staffId, id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu với ID: " + id));

        Unit unit = unitRepository.findById(dto.unitId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + dto.unitId()));

        material.setDescription(dto.description());
        material.setFileUrl(dto.fileUrl());
        material.setType(dto.type());
        material.setUnit(unit);

        Material savedMaterial = materialRepository.save(material);

        // Auto-create approval request for this material update
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                savedMaterial.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật tài liệu {} và tạo yêu cầu phê duyệt thành công", savedMaterial.getId());
        return MaterialMapper.toDto(savedMaterial);
    }

    public void delete(String id, String staffId) {
        log.info("Nhân viên {} xóa tài liệu với ID: {}", staffId, id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu với ID: " + id));

        // Auto-create approval request for this material deletion
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.MATERIAL,
                material.getId(),
                ApprovalRequest.RequestType.DEACTIVATE,
                staffId
        );

        log.info("Đã tạo yêu cầu xóa tài liệu {} thành công", id);
    }
}
