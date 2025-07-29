package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.mapper.UnitMapper;
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
    public List<UnitResponseDto> list() {
        return unitRepo.findAll()
                .stream()
                .map(UnitMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UnitResponseDto> findByChapterId(String chapterId) {
        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Unit chương với ID: " + chapterId));

        return unitRepo.findByChapter(chapter)
                .stream()
                .map(UnitMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnitResponseDto get(String id) {
        return UnitMapper.toDto(unitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found")));
    }

    /* ---------- CREATE ---------- */
    public UnitResponseDto create(@Valid UnitRequestDto dto, Long staffId) {
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
        return UnitMapper.toDto(savedUnit);
    }

    /* ---------- UPDATE ---------- */
    public UnitResponseDto update(String currentId, @Valid UnitRequestDto dto, Long staffId) {
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

        Unit updatedUnit = unitRepo.save(unit);

        // Auto-create approval request for this updated unit
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.UNIT,
                updatedUnit.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật đơn vị học tập {} và yêu cầu phê duyệt thành công", updatedUnit.getId());
        return UnitMapper.toDto(updatedUnit);
    }

    /* ---------- DELETE ---------- */
    public void delete(String id) {
        log.info("Xóa đơn vị học tập với mã: {}", id);

        Unit unit = unitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập"));

        unitRepo.delete(unit);

        log.info("Xóa đơn vị học tập {} thành công", id);
    }
}