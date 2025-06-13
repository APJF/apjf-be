package fu.sep.cms.controller;

import fu.sep.cms.entity.Subject;
import fu.sep.cms.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/list")
    public List<Subject> getAllSubjects() {
        return subjectService.findAll();
    }

    @GetMapping("/search")
    public List<Subject> searchByTitle(@RequestParam String keyword) {
        return subjectService.findByTitle(keyword);
    }

    @GetMapping("/sort")
    public List<Subject> sortByTitle(@RequestParam(defaultValue = "true") boolean asc) {
        return subjectService.sortByTitle(asc);
    }
}
