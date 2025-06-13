package fu.sep.cms.service;

import fu.sep.cms.entity.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> findAll();
    List<Subject> findByTitle(String keyword);
    List<Subject> sortByTitle(boolean ascending);
}
