package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Option;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.OptionRepository;
import fu.sep.apjf.repository.QuestionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public Page<QuestionResponseDto> getAllQuestions(
            int page, int size, String sort, String direction,
            String keyword, EnumClass.QuestionType type,
            EnumClass.QuestionScope scope) {

        // Tạo đối tượng Pageable cho phân trang và sắp xếp
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // Tạo Specification để lọc dữ liệu
        Specification<Question> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo từ khóa (tìm trong nội dung và giải thích)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordPattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate contentPredicate = cb.like(cb.lower(root.get("content")), keywordPattern);
                Predicate explanationPredicate = cb.like(cb.lower(root.get("explanation")), keywordPattern);
                predicates.add(cb.or(contentPredicate, explanationPredicate));
            }

            // Lọc theo loại câu hỏi
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            // Lọc theo phạm vi câu hỏi
            if (scope != null) {
                predicates.add(cb.equal(root.get("scope"), scope));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Thực hiện truy vấn với Specification và Pageable
        Page<Question> questionsPage = questionRepository.findAll(spec, pageable);

        // Chuyển đổi kết quả sang DTO
        return questionsPage.map(QuestionMapper::toResponseDto);
    }

    public QuestionResponseDto findById(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
        return QuestionMapper.toResponseDto(question);
    }

    public QuestionResponseDto createQuestion(QuestionRequestDto questionDto) {
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .content(questionDto.content())
                .correctAnswer(questionDto.correctAnswer())
                .type(questionDto.type())
                .scope(questionDto.scope())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Tạo options nếu có
        if (questionDto.options() != null && !questionDto.options().isEmpty()) {
            questionDto.options().forEach(optionDto -> {
                Option option = Option.builder()
                        .id(UUID.randomUUID().toString())
                        .content(optionDto.content())
                        .isCorrect(optionDto.isCorrect())
                        .question(savedQuestion)
                        .build();
                optionRepository.save(option);
            });
        }

        return QuestionMapper.toResponseDto(savedQuestion);
    }

    public QuestionResponseDto updateQuestion(String id, QuestionRequestDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        question.setContent(questionDto.content());
        question.setCorrectAnswer(questionDto.correctAnswer());
        question.setType(questionDto.type());
        question.setScope(questionDto.scope());

        Question updatedQuestion = questionRepository.save(question);
        return QuestionMapper.toResponseDto(updatedQuestion);
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

}
