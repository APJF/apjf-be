package fu.sep.apjf.service;

import fu.sep.apjf.dto.response.ExamHistoryResponseDto;
import fu.sep.apjf.dto.response.ExamResultResponseDto;
import fu.sep.apjf.entity.ExamResult;
import fu.sep.apjf.mapper.ExamMapper;
import fu.sep.apjf.mapper.ExamResultMapper;
import fu.sep.apjf.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamHistoryService {

    private final ExamResultRepository examResultRepository;
    private final ExamMapper examMapper;
    private final ExamResultMapper resultMapper;

    public List<ExamHistoryResponseDto> getHistoryByUserId(Long userId) {
        return examResultRepository.findByUserId(userId)
                .stream()
                .map(r -> new ExamHistoryResponseDto(
                        String.valueOf(r.getId()),
                        r.getExam().getId(),
                        r.getExam().getTitle(),
                        r.getScore(),
                        r.getStatus(),
                        r.getExam().getType(),
                        r.getSubmittedAt()
                ))
                .toList();
    }

    public ExamResultResponseDto getExamResultDetail(Long resultId) {
        ExamResult result = examResultRepository.findById(resultId).orElseThrow();
        return resultMapper.toDto(result);
    }
}

