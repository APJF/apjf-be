package fu.sep.apjf.service;

import fu.sep.apjf.dto.response.ExamHistoryResponseDto;
import fu.sep.apjf.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamHistoryService {

    private final ExamResultRepository examResultRepository;

    public List<ExamHistoryResponseDto> getHistoryByUserId(Long userId) {
        return examResultRepository.findByUserIdWithExam(userId)
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
}