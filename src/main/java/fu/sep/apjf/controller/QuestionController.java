package fu.sep.apjf.controller;

import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable String id) {
        QuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByType(@PathVariable EnumClass.QuestionType type) {
        List<QuestionDto> questions = questionService.getQuestionsByType(type);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionDto>> searchQuestions(@RequestParam String keyword) {
        List<QuestionDto> questions = questionService.searchQuestions(keyword);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByExamId(@PathVariable String examId) {
        List<QuestionDto> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto questionDto) {
        QuestionDto question = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        QuestionDto question = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
