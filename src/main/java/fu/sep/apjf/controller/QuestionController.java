package fu.sep.apjf.controller;

import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.entity.Question;
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
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Question>> getQuestionsByType(@PathVariable EnumClass.QuestionType type) {
        List<Question> questions = questionService.getQuestionsByType(type);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String keyword) {
        List<Question> questions = questionService.searchQuestions(keyword);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Question>> getQuestionsByExamId(@PathVariable String examId) {
        List<Question> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionDto questionDto) {
        Question question = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        Question question = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
