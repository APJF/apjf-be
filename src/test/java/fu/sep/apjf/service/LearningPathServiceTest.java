package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.dto.response.UnitProgressDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LearningPathServiceTest {

    @InjectMocks
    private LearningPathService learningPathService;

    @Mock
    private LearningPathRepository learningPathRepository;
    @Mock
    private CourseLearningPathRepository courseLearningPathRepository;
    @Mock
    private UnitProgressRepository unitProgressRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private ChapterRepository chapterRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLearningPath() {
        LearningPathRequestDto dto = new LearningPathRequestDto(
                "Title",
                "Description",
                EnumClass.Level.N2,
                "Goal",
                "Skill",
                EnumClass.PathStatus.Studying,
                10,
                1L
        );

        User user = new User();
        LearningPath path = new LearningPath();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(learningPathRepository.save(any())).thenReturn(path);

        LearningPathResponseDto result = learningPathService.createLearningPath(dto);
        assertNotNull(result);
    }


    @Test
    void testUpdateLearningPath() {
        LearningPathRequestDto dto = new LearningPathRequestDto(
                "NewTitle",
                "NewDesc",
                EnumClass.Level.N1,
                "NewGoal",
                "NewSkill",
                EnumClass.PathStatus.Finished,
                12,
                1L
        );

        LearningPath path = new LearningPath();

        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path));
        when(learningPathRepository.save(any())).thenReturn(path);

        LearningPathResponseDto result = learningPathService.updateLearningPath(1L, dto);
        assertNotNull(result);
    }


    @Test
    void testDeleteLearningPath() {
        doNothing().when(learningPathRepository).deleteById(1L);
        learningPathService.deleteLearningPath(1L);
        verify(learningPathRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetLearningPathsByUser() {
        when(learningPathRepository.findByUserId(1L)).thenReturn(List.of(new LearningPath()));
        List<LearningPathResponseDto> result = learningPathService.getLearningPathsByUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testAddCourseToLearningPath() {
        Course course = new Course();
        LearningPath path = new LearningPath();
        CourseOrderDto dto = new CourseOrderDto("c1", 1L, 1);

        when(courseRepository.findById("c1")).thenReturn(Optional.of(course));
        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path));

        learningPathService.addCourseToLearningPath(dto);
        verify(courseLearningPathRepository).save(any());
    }

    @Test
    void testRemoveCourseFromLearningPath() {
        doNothing().when(courseLearningPathRepository).deleteByLearningPathIdAndCourseId(1L, "c1");
        learningPathService.removeCourseFromLearningPath("c1", 1L);
        verify(courseLearningPathRepository, times(1)).deleteByLearningPathIdAndCourseId(1L, "c1");
    }

    @Test
    void testMarkUnitPassed() {
        Unit unit = new Unit();
        User user = new User();

        when(unitRepository.findById("u1")).thenReturn(Optional.of(unit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(unitProgressRepository.save(any())).thenReturn(new UnitProgress());

        UnitProgressDto result = learningPathService.markUnitPassed("u1", 1L);
        assertNotNull(result);
    }
}
