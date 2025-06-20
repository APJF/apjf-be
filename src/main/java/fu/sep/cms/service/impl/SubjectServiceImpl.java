package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Subject;
import fu.sep.cms.repository.SubjectRepository;
import fu.sep.cms.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository repo;

    @Override
    public Subject createSubject(Subject subject) {
        subject.setCreatedAt(now());
        subject.setUpdatedAt(now());
        return repo.save(subject);
    }

    @Override
    public Subject updateSubject(Long id, Subject incoming) {
        Subject s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        s.setTitle(incoming.getTitle());
        s.setTopic(incoming.getTopic());
        s.setDescription(incoming.getDescription());
        s.setLevel(incoming.getLevel());
        s.setEstimatedDuration(incoming.getEstimatedDuration());
        s.setCreatorId(incoming.getCreatorId());
        s.setImage(incoming.getImage());
        s.setOrderNumber(incoming.getOrderNumber());
        s.setUpdatedAt(now());
        return repo.save(s);
    }

    @Override
    public void deleteSubject(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Subject getSubjectById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    @Override
    public Page<Subject> getSubjects(
            String level,
            String creatorId,
            String keyword,
            Pageable pageable
    ) {
        boolean hasLevel = level != null && !level.isBlank();
        boolean hasCreator = creatorId != null && !creatorId.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasLevel && hasCreator && hasKeyword) {
            return repo.findByLevelAndCreatorIdAndTitleContainingIgnoreCase(
                    level, creatorId, keyword, pageable);
        } else if (hasLevel && hasCreator) {
            return repo.findByLevelAndCreatorId(level, creatorId, pageable);
        } else if (hasLevel && hasKeyword) {
            return repo.findByLevelAndTitleContainingIgnoreCase(level, keyword, pageable);
        } else if (hasCreator && hasKeyword) {
            return repo.findByCreatorIdAndTitleContainingIgnoreCase(creatorId, keyword, pageable);
        } else if (hasLevel) {
            return repo.findByLevel(level, pageable);
        } else if (hasCreator) {
            return repo.findByCreatorId(creatorId, pageable);
        } else if (hasKeyword) {
            return repo.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            return repo.findAll(pageable);
        }
    }

    private static java.time.LocalDateTime now() {
        return java.time.LocalDateTime.now();
    }

    public Subject getDetail(Long id) {
        return repo
                .findWithChaptersAndSlotsById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }
}
