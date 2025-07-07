package fu.sep.apjf.service;

import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.repository.ChapterRepository;
import fu.sep.apjf.repository.UnitRepository;
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

    /*------------- READ -------------*/
    @Transactional(readOnly = true)
    public List<UnitDto> list() {
        return unitRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnitDto get(String id) {
        return toDto(unitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found")));
    }

    /* ---------- CREATE ---------- */
    public UnitDto create(@Valid UnitDto dto, String staffId) {
        log.info("Nhân viên {} tạo đơn vị học tập mới với mã: {}", staffId, dto.id());

        Chapter parent = chapterRepo.findById(dto.chapterId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        if (unitRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Mã đơn vị học tập đã tồn tại");

        Unit unit = Unit.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(EnumClass.Status.DRAFT) // Set as DRAFT until approved
                .chapter(parent)
                .build();

        // Set prerequisite unit if provided
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập tiên quyết"));
            unit.setPrerequisiteUnit(prerequisite);
        }

        Unit savedUnit = unitRepo.save(unit);

        // Auto-create approval request for this new unit
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                savedUnit.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo đơn vị học tập {} và yêu cầu phê duyệt thành công", savedUnit.getId());
        return toDto(savedUnit);
    }

    /* ---------- UPDATE ---------- */
    public UnitDto update(String currentId, @Valid UnitDto dto, String staffId) {
        log.info("Nhân viên {} cập nhật đơn vị học tập với mã: {}", staffId, currentId);

        Unit unit = unitRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập"));

        unit.setTitle(dto.title());
        unit.setDescription(dto.description());
        unit.setStatus(EnumClass.Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite unit
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập tiên quyết"));
            unit.setPrerequisiteUnit(prerequisite);
        } else {
            unit.setPrerequisiteUnit(null);
        }

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (unitRepo.existsById(dto.id()))
                throw new IllegalArgumentException("Mã đơn vị học tập mới đã tồn tại");
            unitRepo.delete(unit);
            unit.setId(dto.id());
        }

        Unit savedUnit = unitRepo.save(unit);

        // Auto-create approval request for this unit update
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                savedUnit.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật đơn vị học tập {} và tạo yêu cầu phê duyệt thành công", savedUnit.getId());
        return toDto(savedUnit);
    }

    /* ---------- Mapping helpers ---------- */
    private UnitDto toDto(Unit u) {
        return new UnitDto(u.getId(), u.getTitle(), u.getDescription(),
                u.getStatus(), u.getChapter().getId(),
                u.getPrerequisiteUnit() != null ? u.getPrerequisiteUnit().getId() : null);
    }
}