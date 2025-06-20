package fu.sep.cms.service;

import fu.sep.cms.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectService {
    Subject createSubject(Subject subject);
    Subject updateSubject(Long id, Subject subject);
    void deleteSubject(Long id);
    Subject getSubjectById(Long id);

    Page<Subject> getSubjects(
            String level,
            String creatorId,
            String keyword,
            Pageable pageable
    );
    Subject getDetail(Long subjectId);
}
