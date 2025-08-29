package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.CourseLearningPathMapper;
import fu.sep.apjf.mapper.LearningPathMapper;
import fu.sep.apjf.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.Instant;
import java.util.*;

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
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LearningPathMapper learningPathMapper;

    @Mock
    private CourseLearningPathMapper courseLearningPathMapper;

    @Mock
    private CourseProgressRepository courseProgressRepository;

    @Mock
    private UnitProgressRepository unitProgressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLearningPathById_ReturnsDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setTitle("LP1");
        path.setDescription("Description");
        path.setTargetLevel(EnumClass.Level.N5_L.name());
        path.setPrimaryGoal("Skill");
        path.setFocusSkill("Focus");
        path.setStatus(EnumClass.PathStatus.PENDING);
        path.setCreatedAt(Instant.now());
        path.setLastUpdatedAt(Instant.now());

        Course course = new Course();
        course.setId("C1");
        course.setTitle("Course 1");
        course.setDescription("Desc");
        course.setDuration(10f);
        course.setLevel(EnumClass.Level.N5_L);

        CourseLearningPath clp = new CourseLearningPath();
        clp.setCourse(course);
        clp.setLearningPath(path);
        path.setCourseLearningPaths(List.of(clp));

        when(learningPathRepository.findByIdWithCourses(1L)).thenReturn(Optional.of(path));
        when(courseProgressRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());

        LearningPathDetailResponseDto dto = learningPathService.getLearningPathById(1L, user);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("LP1", dto.title());
        assertEquals(0f, dto.percent());
    }

    @Test
    void testCreateLearningPath_ThrowsException_WhenNoCourses() {
        // Mock userRepository trả về User hợp lệ
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        LearningPathRequestDto dto = new LearningPathRequestDto(
                1L, "Title", "Desc", EnumClass.Level.N5_L, "Goal", "Skill", 10f, 1L, null
        );

        assertThrows(IllegalArgumentException.class,
                () -> learningPathService.createLearningPath(dto, 1L));
    }


    @Test
    void testSetStudyingLearningPath_SetsStatus() {
        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setStatus(EnumClass.PathStatus.PENDING);

        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path));

        learningPathService.setStudyingLearningPath(1L, 1L);

        assertEquals(EnumClass.PathStatus.STUDYING, path.getStatus());
        verify(learningPathRepository).save(path);
    }

    @Test
    void testCalculateCourseProgress_ReturnsCorrectPercentage() {
        User user = new User();
        user.setId(1L);

        when(unitProgressRepository.countChaptersByCourseId("C1")).thenReturn(5L);
        when(unitProgressRepository.countCompletedChaptersByUserAndCourse(user, "C1")).thenReturn(3L);

        float progress = learningPathService.calculateCourseProgress(user, "C1");

        assertEquals(60f, progress);
    }

    @Test
    void testDeleteLearningPath_DeletesSuccessfully() {
        learningPathService.deleteLearningPath(1L);

        verify(courseLearningPathRepository).deleteAll(anyList());
        verify(learningPathRepository).deleteById(1L);
    }

}
