//package fu.sep.apjf.service;
//
//import fu.sep.apjf.dto.request.LearningPathRequestDto;
//import fu.sep.apjf.dto.response.CourseOrderDto;
//import fu.sep.apjf.dto.response.LearningPathDetailResponseDto;
//import fu.sep.apjf.dto.response.LearningPathResponseDto;
//import fu.sep.apjf.entity.*;
//import fu.sep.apjf.mapper.CourseLearningPathMapper;
//import fu.sep.apjf.mapper.LearningPathMapper;
//import fu.sep.apjf.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class LearningPathServiceTest {
//
//    @InjectMocks
//    private LearningPathService learningPathService;
//
//    @Mock
//    private LearningPathRepository learningPathRepository;
//
//    @Mock
//    private CourseLearningPathRepository courseLearningPathRepository;
//
//    @Mock
//    private CourseRepository courseRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private LearningPathMapper learningPathMapper;
//
//    @Mock
//    private CourseLearningPathMapper courseLearningPathMapper;
//
//    @Mock
//    private CourseProgressRepository courseProgressRepository;
//
//    @Mock
//    private LearningPathProgressRepository learningPathProgressRepository;
//
//    private User testUser;
//    private LearningPath testPath;
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUsername("user1");
//
//        testPath = new LearningPath();
//        testPath.setId(10L);
//        testPath.setTitle("LP1");
//        testPath.setDescription("Description");
//        testPath.setTargetLevel(EnumClass.Level.N3.toString());
//        testPath.setUser(testUser);
//        testPath.setCreatedAt(Instant.now());
//        testPath.setLastUpdatedAt(Instant.now());
//    }
//
//    @Test
//    void testCreateLearningPath_Success() {
//        LearningPathRequestDto dto = new LearningPathRequestDto(
//                1L,
//                "Description",
//                "des",
//                EnumClass.Level.N3,
//                "PrimaryGoal",
//                "FocusSkill",
//                10f,
//                5L,
//                List.of("course1", "course2")
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(learningPathMapper.toEntity(dto, testUser)).thenReturn(testPath);
//        when(learningPathRepository.save(any(LearningPath.class))).thenReturn(testPath);
//        when(courseRepository.findById("course1")).thenReturn(Optional.of(new Course()));
//        when(courseRepository.findById("course2")).thenReturn(Optional.of(new Course()));
//        when(courseLearningPathRepository.findByLearningPathId(testPath.getId())).thenReturn(List.of());
//        when(courseLearningPathMapper.toDto(any())).thenReturn(new CourseOrderDto("course1", 0L));
//
//        LearningPathResponseDto response = learningPathService.createLearningPath(dto, 1L);
//
//        assertNotNull(response);
//        assertEquals("LP1", response.title());
//        verify(learningPathRepository, times(1)).save(any(LearningPath.class));
//        verify(courseLearningPathRepository, times(1)).saveAll(any());
//    }
//
//    @Test
//    void testGetLearningPathById_Success() {
//        when(learningPathRepository.findByIdWithCourses(10L)).thenReturn(Optional.of(testPath));
//
//        LearningPathDetailResponseDto result = learningPathService.getLearningPathById(10L);
//
//        assertNotNull(result);
//        assertEquals("LP1", result.title());
//    }
//
//    @Test
//    void testUpdateLearningPath_Success() {
//        LearningPathRequestDto dto = new LearningPathRequestDto(
//                "LP1 Updated",
//                "Updated Desc",
//                EnumClass.Level.N2.toString(),
//                "Goal",
//                "Skill",
//                20,
//                List.of("course1")
//        );
//
//        when(learningPathRepository.findById(10L)).thenReturn(Optional.of(testPath));
//        when(courseRepository.findById("course1")).thenReturn(Optional.of(new Course("course1")));
//        when(courseLearningPathMapper.toDto(any())).thenReturn(new CourseOrderDto("course1", 0));
//
//        LearningPathResponseDto response = learningPathService.updateLearningPath(10L, dto, 1L);
//
//        assertNotNull(response);
//        verify(learningPathRepository, times(1)).save(testPath);
//        verify(courseLearningPathRepository, times(1)).saveAll(any());
//    }
//
//    @Test
//    void testAddCourseToLearningPath_AlreadyExists() {
//        CourseOrderDto dto = new CourseOrderDto("course1", 0);
//        Course course = new Course("course1");
//
//        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
//        when(learningPathRepository.findById(10L)).thenReturn(Optional.of(testPath));
//        when(courseLearningPathRepository.findByLearningPathId(10L)).thenReturn(List.of(
//                new CourseLearningPath(new CourseLearningPathKey("course1", 10L), course, testPath, 0)
//        ));
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//                learningPathService.addCourseToLearningPath(10L, dto));
//
//        assertEquals("Khóa học đã tồn tại trong lộ trình.", exception.getMessage());
//    }
//
//    @Test
//    void testReorderCoursesInPath_Success() {
//        Course course1 = new Course("course1");
//        Course course2 = new Course("course2");
//
//        CourseLearningPath clp1 = new CourseLearningPath(new CourseLearningPathKey("course1", 10L), course1, testPath, 0);
//        CourseLearningPath clp2 = new CourseLearningPath(new CourseLearningPathKey("course2", 10L), course2, testPath, 1);
//
//        when(courseLearningPathRepository.findByLearningPathId(10L)).thenReturn(List.of(clp1, clp2));
//
//        learningPathService.reorderCoursesInPath(10L, List.of("course2", "course1"));
//
//        assertEquals(0, clp2.getCourseOrderNumber());
//        assertEquals(1, clp1.getCourseOrderNumber());
//        verify(courseLearningPathRepository, times(2)).save(any());
//    }
//}
