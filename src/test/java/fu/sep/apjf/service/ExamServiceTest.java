package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamOverviewResponseDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ExamMapper;
import fu.sep.apjf.mapper.ExamOverviewMapper;
import fu.sep.apjf.mapper.QuestionMapper;
import fu.sep.apjf.repository.ExamRepository;
import fu.sep.apjf.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ExamMapper examMapper;

    @Mock
    private ExamOverviewMapper examOverviewMapper;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private ExamService examService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExam() {
        ExamRequestDto dto = new ExamRequestDto(
                "id1",
                "title",
                "description",
                60f,// <-- đây là problem
                EnumClass.ExamType.WRITING,     // type
                EnumClass.ExamScopeType.COURSE,
                EnumClass.GradingMethod.MANUAL,
                 // examScopeType
                "courseId1",
                null,
                null,
                null
        );
        Exam exam = new Exam();
        ExamResponseDto dtoResult = new ExamResponseDto(
                "E1",                          // id
                "Title example",                // title
                "Description example",          // description
                60.0f,                          // duration
                EnumClass.ExamType.WRITING,     // type
                EnumClass.ExamScopeType.COURSE, // examScopeType
                EnumClass.GradingMethod.MANUAL, // gradingMethod
                "C1",                           // courseId
                null,                           // chapterId
                null,                           // unitId
                Instant.now(),                  // createdAt
                10                              // totalQuestions
        );

        when(examMapper.toEntity(dto)).thenReturn(exam);
        when(examRepository.save(exam)).thenReturn(exam);
        when(examMapper.toDto(exam)).thenReturn(dtoResult);

        ExamResponseDto result = examService.create(dto);

        assertNotNull(result);
        verify(examRepository).save(exam);
    }

    @Test
    void testUpdateExam_NotFound() {
        String id = "E1";
        ExamRequestDto dto = mock(ExamRequestDto.class);

        when(examRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> examService.update(id, dto));
    }

    @Test
    void testDeleteExam_Success() {
        String id = "E1";
        Exam exam = new Exam();
        when(examRepository.findById(id)).thenReturn(Optional.of(exam));

        examService.delete(id);

        verify(examRepository).delete(exam);
    }

    @Test
    void testDeleteExam_NotFound() {
        String id = "E1";
        when(examRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> examService.delete(id));
    }

    @Test
    void testAddQuestions_Success() {
        // Given
        String examId = "EX1";
        List<String> questionIds = List.of("Q1", "Q2");

        Exam exam = new Exam();
        exam.setId(examId);

        // Mock ExamRepository
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        // Mock QuestionRepository trả về đủ số lượng câu hỏi
        Question q1 = new Question();
        q1.setId("Q1");
        Question q2 = new Question();
        q2.setId("Q2");

        when(questionRepository.findAllById(questionIds)).thenReturn(List.of(q1, q2));

        // Mock lưu exam
        when(examRepository.save(any(Exam.class))).thenReturn(exam);

        // When
        examService.addQuestions(examId, questionIds); // gọi thẳng, không gán


        // Then
        verify(examRepository, times(1)).save(any(Exam.class));
    }


    @Test
    void testAddQuestions_NotFoundQuestion() {
        String examId = "E1";
        List<String> questionIds = List.of("Q1", "Q2");
        Exam exam = new Exam();
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(questionRepository.findAllById(questionIds)).thenReturn(Collections.singletonList(new Question()));

        assertThrows(ResourceNotFoundException.class, () -> examService.addQuestions(examId, questionIds));
    }

    @Test
    void testGetOverview_Success() {
        String examId = "E1";
        Exam exam = new Exam();
        ExamOverviewResponseDto dto = new ExamOverviewResponseDto(
                "EX1",               // examId
                "Sample Exam",       // title
                "Description here",  // description
                90.0f,               // duration
                20,                  // totalQuestions
                EnumClass.ExamType.WRITING          // type
        );
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examOverviewMapper.toDto(exam)).thenReturn(dto);

        ExamOverviewResponseDto result = examService.getOverview(examId);
        assertNotNull(result);
        assertEquals(dto, result);
    }

    // Có thể thêm các test cho getExamDetail, getQuestionsByExamId, removeQuestions tương tự
}
