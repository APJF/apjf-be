package fu.sep.apjf.service;

import fu.sep.apjf.dto.QuestionDto;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.QuestionOption;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.repository.QuestionRepository;
import fu.sep.apjf.repository.QuestionOptionRepository;
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

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
    }

    public List<Question> getQuestionsByType(EnumClass.QuestionType type) {
        return questionRepository.findByType(type);
    }

    public List<Question> searchQuestions(String keyword) {
        return questionRepository.findByContentContaining(keyword);
    }

    public Question createQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .content(questionDto.getContent())
                .correctAnswer(questionDto.getCorrectAnswer())
                .type(questionDto.getType())
                .explanation(questionDto.getExplanation())
                .fileUrl(questionDto.getFileUrl())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Tạo options nếu có
        if (questionDto.getOptions() != null && !questionDto.getOptions().isEmpty()) {
            questionDto.getOptions().forEach(optionDto -> {
                QuestionOption option = QuestionOption.builder()
                        .id(UUID.randomUUID().toString())
                        .content(optionDto.getContent())
                        .isCorrect(optionDto.getIsCorrect())
                        .question(savedQuestion)
                        .build();
                questionOptionRepository.save(option);
            });
        }

        return savedQuestion;
    }

    public Question updateQuestion(String id, QuestionDto questionDto) {
        Question question = getQuestionById(id);

        question.setContent(questionDto.getContent());
        question.setCorrectAnswer(questionDto.getCorrectAnswer());
        question.setType(questionDto.getType());
        question.setExplanation(questionDto.getExplanation());
        question.setFileUrl(questionDto.getFileUrl());

        return questionRepository.save(question);
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getQuestionsByExamId(String examId) {
        return questionRepository.findByExamId(examId);
    }
}
