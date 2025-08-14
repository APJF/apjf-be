package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Option;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.mapper.OptionMapper;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.OptionRepository;
import fu.sep.apjf.repository.QuestionRepository;
import fu.sep.apjf.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UnitRepository unitRepository;
    private final QuestionMapper questionMapper;
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    public QuestionResponseDto createQuestion(QuestionRequestDto dto) {
        Set<Unit> units = dto.unitIds() == null ? Set.of() :
                dto.unitIds().stream()
                        .map(id -> unitRepository.findById(id).orElseThrow())
                        .collect(Collectors.toSet());
        Question question = questionMapper.toEntity(dto);
        question.setUnits(units);
        return questionMapper.toDto(questionRepository.save(question));
    }

    public QuestionResponseDto updateQuestion(String id, QuestionRequestDto dto) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setContent(dto.content());
        question.setExplanation(dto.explanation());
        question.setType(dto.type());
        Set<Unit> updatedUnits = dto.unitIds() == null ? Set.of() :
                dto.unitIds().stream()
                        .map(unitId -> unitRepository.findById(unitId).orElseThrow())
                        .collect(Collectors.toSet());
        question.setUnits(updatedUnits);
        return questionMapper.toDto(questionRepository.save(question));
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    public Page<QuestionResponseDto> getAllQuestions(String questionId, String unitId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.searchQuestionsWithoutOptions(questionId, unitId, pageable)
                .map(q -> new QuestionResponseDto(
                        q.getId(),
                        q.getContent(),
                        q.getScope(),
                        q.getType(),
                        q.getFileUrl(),
                        q.getCreatedAt(),
                        null,
                        q.getUnits().stream().map(Unit::getId).toList()
                ));
    }

    public List<QuestionResponseDto> getQuestionsByExamId(String examId) {
        List<Question> questions = questionRepository.findByExamIdWithOptionsAndUnits(examId);
        return questions.stream()
                .map(questionMapper::toDto) // mapper sang QuestionResponseDto
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        return questionMapper.toDto(question);
    }

    public List<OptionResponseDto> getOptionsByQuestionId(String questionId) {
        List<Option> options = optionRepository.findByQuestionId(questionId);
        return options.stream().map(optionMapper::toDto).toList();
    }

    public OptionResponseDto createOption(String questionId, OptionRequestDto dto) {
        Question question = questionRepository.findById(questionId).orElseThrow();
        Option option = optionMapper.toEntity(dto);
        option.setQuestion(question);
        return optionMapper.toDto(optionRepository.save(option));
    }
}