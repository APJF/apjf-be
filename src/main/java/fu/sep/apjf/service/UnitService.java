package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.UnitDetailProgressDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.UnitMapper;
import fu.sep.apjf.repository.ChapterRepository;
import fu.sep.apjf.repository.UnitProgressRepository;
import fu.sep.apjf.repository.UnitRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class UnitService {

    private final UnitRepository unitRepo;
    private final ChapterRepository chapterRepo;
    private final ApprovalRequestService approvalRequestService;
    private final UnitMapper unitMapper;
    private final UserRepository userRepository;
    private final UnitProgressRepository unitProgressRepository;

    @Transactional(readOnly = true)
    public List<UnitResponseDto> list() {
        return unitRepo.findAll()
                .stream()
                .map(unitMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UnitResponseDto> findByChapterId(String chapterId) {
        List<Unit> units = unitRepo.findByChapterId(chapterId);

        // Lazy validation: chỉ check chapter existence nếu list rỗng để tối ưu queries
        if (units.isEmpty() && !chapterRepo.existsById(chapterId)) {
            throw new EntityNotFoundException("Không tìm thấy chapter với ID: " + chapterId);
        }

        return units.stream()
                .map(unitMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnitResponseDto findById(String id) {
        return unitMapper.toDto(unitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public UnitDetailProgressDto getUnitDetailById(String unitId, Long userId) {
        // Lấy unit
        Unit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with ID: " + unitId));

        // Lấy progress của user cho unit này
        UnitProgress progress = unitProgressRepository.findByUserIdAndUnitId(userId, unitId)
                .orElse(null);

        boolean isCompleted = progress != null && progress.isCompleted();

        // Map sang DTO
        return new UnitDetailProgressDto(
                unit.getId(),
                unit.getTitle(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getChapter() != null ? unit.getChapter().getId() : null,
                unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null,
                isCompleted
        );
    }


    /* ---------- CREATE ---------- */
    public UnitResponseDto create(@Valid UnitRequestDto dto, Long staffId) {
        log.info("Nhân viên {} tạo đơn vị học tập mới với mã: {}", staffId, dto.id());

        Chapter parent = chapterRepo.findById(dto.chapterId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        if (unitRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Mã đơn vị học tập đã tồn tại");

        // Sử dụng mapper để tạo Unit entity
        Unit unit = unitMapper.toEntity(dto);
        unit.setStatus(EnumClass.Status.INACTIVE); // Set as INACTIVE until approved
        unit.setChapter(parent);

        // Set prerequisite unit if provided
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập tiên quyết"));
            unit.setPrerequisiteUnit(prerequisite);
        }

        Unit savedUnit = unitRepo.save(unit);

        // Auto-create approval request for the new unit
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                savedUnit.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo đơn vị học tập {} và yêu cầu phê duyệt thành công", savedUnit.getId());
        return unitMapper.toDto(savedUnit);
    }

    /* ---------- UPDATE ---------- */
    public UnitResponseDto update(String currentId, UnitRequestDto dto, Long staffId) {
        log.info("Nhân viên {} cập nhật đơn vị học tập với mã: {}", staffId, currentId);

        Unit existingUnit = unitRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập"));

        // Cập nhật các trường của Unit hiện có thay vì tạo mới
        existingUnit.setTitle(dto.title());
        existingUnit.setDescription(dto.description());
        existingUnit.setStatus(EnumClass.Status.INACTIVE); // Reset to INACTIVE when updated

        // Cập nhật chapter nếu khác
        if (!existingUnit.getChapter().getId().equals(dto.chapterId())) {
            Chapter newChapter = chapterRepo.findById(dto.chapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));
            existingUnit.setChapter(newChapter);
        }

        // Cập nhật prerequisite unit
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập tiên quyết"));
            existingUnit.setPrerequisiteUnit(prerequisite);
        } else {
            existingUnit.setPrerequisiteUnit(null);
        }

        Unit savedUnit = unitRepo.save(existingUnit);

        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                savedUnit.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật đơn vị học tập {} và yêu cầu phê duyệt thành công", savedUnit.getId());
        return unitMapper.toDto(savedUnit);
    }

    public UnitResponseDto deactivate(String unitId, Long staffId) {

        // Check role STAFF
        if (!userRepository.existsById(staffId)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên");
        }

        // Tìm unit
        Unit existingUnit = unitRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập"));

        // Nếu đã INACTIVE thì không cần đổi nữa
        if (existingUnit.getStatus() == EnumClass.Status.INACTIVE) {
            throw new IllegalStateException("Đơn vị học tập đã ở trạng thái INACTIVE");
        }

        // Cập nhật status thành INACTIVE
        existingUnit.setStatus(EnumClass.Status.INACTIVE);

        Unit savedUnit = unitRepo.save(existingUnit);

        return unitMapper.toDto(savedUnit);
    }

}