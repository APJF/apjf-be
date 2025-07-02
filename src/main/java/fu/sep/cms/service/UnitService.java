package fu.sep.cms.service;

import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Status;
import fu.sep.cms.entity.Unit;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.UnitRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UnitService {

    private final UnitRepository unitRepo;
    private final ChapterRepository chapterRepo;

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
    public UnitDto create(@Valid UnitDto dto) {
        Chapter parent = chapterRepo.findById(dto.chapterId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter missing"));

        if (unitRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Unit id already exists");

        Unit u = Unit.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(Status.DRAFT)
                .chapter(parent)
                .build();

        // Set prerequisite unit if provided
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Prerequisite unit not found"));
            u.setPrerequisiteUnit(prerequisite);
        }

        return toDto(unitRepo.save(u));
    }

    /* ---------- UPDATE ---------- */
    public UnitDto update(String currentId, @Valid UnitDto dto) {
        Unit u = unitRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));

        u.setTitle(dto.title());
        u.setDescription(dto.description());
        u.setStatus(Status.DRAFT);

        // Update prerequisite unit
        if (dto.prerequisiteUnitId() != null) {
            Unit prerequisite = unitRepo.findById(dto.prerequisiteUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Prerequisite unit not found"));
            u.setPrerequisiteUnit(prerequisite);
        } else {
            u.setPrerequisiteUnit(null);
        }

        if (!dto.id().equals(currentId)) {
            if (unitRepo.existsById(dto.id()))
                throw new IllegalArgumentException("New unit id already exists");
            unitRepo.delete(u);
            u.setId(dto.id());
        }
        return toDto(unitRepo.save(u));
    }

    /*------------- helper -------------*/
    private UnitDto toDto(Unit u) {
        return new UnitDto(u.getId(), u.getTitle(), u.getDescription(),
                u.getStatus(), u.getChapter().getId(),
                u.getPrerequisiteUnit() != null ? u.getPrerequisiteUnit().getId() : null);
    }
}