package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.QuestionRepository;
import fu.sep.apjf.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UnitRepository unitRepository;
    private final QuestionMapper questionMapper;

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
                        q.getExplanation(),
                        q.getFileUrl(),
                        q.getCreatedAt(),
                        null,
                        q.getUnits().stream().map(Unit::getId).toList()
                ));
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        return questionMapper.toDto(question);
    }
}