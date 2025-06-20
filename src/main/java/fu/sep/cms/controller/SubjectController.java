package fu.sep.cms.controller;

import fu.sep.cms.entity.Subject;
import fu.sep.cms.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;

    @PostMapping
    public ResponseEntity<Subject> create(@RequestBody Subject subject) {
        return ResponseEntity.status(201).body(service.createSubject(subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(
            @PathVariable Long id,
            @RequestBody Subject subject
    ) {
        return ResponseEntity.ok(service.updateSubject(id, subject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSubjectById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Subject>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) String keyword
    ) {
        Sort sort = "asc".equalsIgnoreCase(dir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Subject> result = service.getSubjects(level, creatorId, keyword, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Subject> detail(@PathVariable Long id) {
        Subject subject = service.getDetail(id);
        return ResponseEntity.ok(subject);
    }
}
