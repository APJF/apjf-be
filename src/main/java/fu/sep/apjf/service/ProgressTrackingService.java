package fu.sep.apjf.service;

import fu.sep.apjf.entity.*;
import fu.sep.apjf.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressTrackingService {

    private final UnitRepository unitRepository;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UnitProgressRepository unitProgressRepository;

    @Transactional
    public void markUnitPassed(String unitId, Long userId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        // Nếu đã pass rồi thì không làm gì
        UnitProgress progress = UnitProgress.builder()
                .id(new UnitProgressKey(unitId, userId))
                .unit(unit)
                .user(user)
                .completed(true)
                .build();
        unitProgressRepository.save(progress);
    }

}
