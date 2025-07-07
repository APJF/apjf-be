package fu.sep.cms.service;

import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.ApprovalRequest;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.EnumClass;
import fu.sep.cms.entity.Unit;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.UnitRepository;
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
        log.info("Staff {} creating new unit with ID: {}", staffId, dto.id());

        Chapter parent = chapterRepo.findById(dto.chapterId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter missing"));

        if (unitRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Unit id already exists");

        Unit u = Unit.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(EnumClass.Status.DRAFT) // Set as DRAFT until approved
                .chapter(parent)
                .build();

        // Set prerequisite unit if provided
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite unit not found"));
            u.setPrerequisiteUnit(prerequisite);
        }

        Unit savedUnit = unitRepo.save(u);

        // Auto-create approval request for this new unit
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                savedUnit.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Successfully created unit {} and approval request", savedUnit.getId());
        return toDto(savedUnit);
    }

    /* ---------- UPDATE ---------- */
    public UnitDto update(String currentId, @Valid UnitDto dto, String staffId) {
        log.info("Staff {} updating unit with ID: {}", staffId, currentId);

        Unit unit = unitRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));

        unit.setTitle(dto.title());
        unit.setDescription(dto.description());
        unit.setStatus(EnumClass.Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite unit
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite unit not found"));
            unit.setPrerequisiteUnit(prerequisite);
        } else {
            unit.setPrerequisiteUnit(null);
        }

        /* Đổi PK nếu khác */
        if (!dto.id().equals(currentId)) {
            if (unitRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New unit id already exists");
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

        log.info("Successfully updated unit {} and created approval request", savedUnit.getId());
        return toDto(savedUnit);
    }

    /* ---------- Mapping helpers ---------- */
    private UnitDto toDto(Unit u) {
        return new UnitDto(u.getId(), u.getTitle(), u.getDescription(),
                u.getStatus(), u.getChapter().getId(),
                u.getPrerequisiteUnit() != null ? u.getPrerequisiteUnit().getId() : null);
    }
}