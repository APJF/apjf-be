package fu.sep.apjf.service;

import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.QuestionOption;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.ExamRepository;
import fu.sep.apjf.repository.QuestionOptionRepository;
import fu.sep.apjf.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ExamRepository examRepository;

    public List<QuestionDto> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return QuestionMapper.toDtoList(questions);
    }

    public QuestionDto getQuestionById(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
        return QuestionMapper.toDto(question);
    }

    public List<QuestionDto> getQuestionsByType(EnumClass.QuestionType type) {
        List<Question> questions = questionRepository.findByType(type);
        return QuestionMapper.toDtoList(questions);
    }

    public List<QuestionDto> searchQuestions(String keyword) {
        List<Question> questions = questionRepository.findByContentContainingIgnoreCase(keyword);
        return QuestionMapper.toDtoList(questions);
    }

    public QuestionDto createQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .content(questionDto.content())
                .correctAnswer(questionDto.correctAnswer())
                .type(questionDto.type())
                .scope(questionDto.scope())
                .explanation(questionDto.explanation())
                .fileUrl(questionDto.fileUrl())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Tạo options nếu có
        if (questionDto.options() != null && !questionDto.options().isEmpty()) {
            questionDto.options().forEach(optionDto -> {
                QuestionOption option = QuestionOption.builder()
                        .id(UUID.randomUUID().toString())
                        .content(optionDto.content())
                        .isCorrect(optionDto.isCorrect())
                        .question(savedQuestion)
                        .build();
                questionOptionRepository.save(option);
            });
        }

        return QuestionMapper.toDto(savedQuestion);
    }

    public QuestionDto updateQuestion(String id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        question.setContent(questionDto.content());
        question.setCorrectAnswer(questionDto.correctAnswer());
        question.setType(questionDto.type());
        question.setScope(questionDto.scope());
        question.setExplanation(questionDto.explanation());
        question.setFileUrl(questionDto.fileUrl());

        Question updatedQuestion = questionRepository.save(question);
        return QuestionMapper.toDto(updatedQuestion);
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    public List<QuestionDto> getQuestionsByExamId(String examId) {
        return examRepository.findById(examId)
                .map(exam -> {
                    List<Question> questions = questionRepository.findByExamsContaining(exam);
                    return QuestionMapper.toDtoList(questions);
                })
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
    }
}
