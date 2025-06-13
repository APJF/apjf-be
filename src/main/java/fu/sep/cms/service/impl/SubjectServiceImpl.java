package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Subject;
import fu.sep.cms.repository.SubjectRepository;
import fu.sep.cms.service.SubjectService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    public List<Subject> findByTitle(String keyword) {
        return subjectRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public List<Subject> sortByTitle(boolean ascending) {
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, "title");
        return subjectRepository.findAll(sort);
    }
}
