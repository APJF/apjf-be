package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ApprovalDecisionDto;
import fu.sep.apjf.dto.response.ApprovalRequestDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import fu.sep.apjf.mapper.ApprovalRequestMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalRequestServiceTest {

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApprovalRequestMapper approvalRequestMapper;

    @InjectMocks
    private ApprovalRequestService approvalRequestService;

    private User creator;
    private ApprovalRequest approvalRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        creator = new User();
        creator.setId(1L);
        creator.setEmail("creator@example.com");

        approvalRequest = ApprovalRequest.builder()
                .id(1)
                .targetType(TargetType.COURSE)
                .requestType(RequestType.CREATE)
                .decision(Decision.PENDING)
                .creator(creator)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnApprovalRequestDtos() {
        ApprovalRequestDto dto = new ApprovalRequestDto(
                1,                                    // id
                TargetType.COURSE,                    // targetType
                "COURSE123",                          // targetId
                RequestType.CREATE,                   // requestType
                Decision.PENDING,                     // decision
                "Please review this course",          // feedback
                "John Doe",                           // createdBy
                Instant.now(),                        // createdAt
                "Admin",                              // reviewedBy
                Instant.now()                         // reviewedAt
        );

        // Given
        when(approvalRequestRepository.findAll()).thenReturn(List.of(approvalRequest));
        when(approvalRequestMapper.toDtoList(any())).thenReturn(List.of(dto));

        // When
        List<ApprovalRequestDto> result = approvalRequestService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(approvalRequestRepository).findAll();
    }

    @Test
    void autoCreateApprovalRequest_ShouldCreate_WhenNotExists() {
        // Given
        String targetId = "course1";
        Course course = new Course();
        course.setId(targetId);

        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(approvalRequestRepository.findPendingRequestByTargetId(targetId)).thenReturn(Optional.empty());
        when(courseRepository.findById(targetId)).thenReturn(Optional.of(course));
        when(approvalRequestRepository.save(any(ApprovalRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        approvalRequestService.autoCreateApprovalRequest(TargetType.COURSE, targetId, RequestType.CREATE, 1L);

        // Then
        verify(approvalRequestRepository).save(any(ApprovalRequest.class));
    }

    @Test
    void autoCreateApprovalRequest_ShouldSkip_WhenPendingRequestExists() {
        // Given
        String targetId = "course1";

        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(approvalRequestRepository.findPendingRequestByTargetId(targetId))
                .thenReturn(Optional.of(approvalRequest));

        // When
        approvalRequestService.autoCreateApprovalRequest(TargetType.COURSE, targetId, RequestType.CREATE, 1L);

        // Then
        verify(approvalRequestRepository, never()).save(any());
    }

    @Test
    void processApproval_ShouldApproveRequestAndUpdateEntity() {

        ApprovalRequestDto dto = new ApprovalRequestDto(
                1,                                    // id
                TargetType.COURSE,                    // targetType
                "COURSE123",                          // targetId
                RequestType.CREATE,                   // requestType
                Decision.PENDING,                     // decision
                "Please review this course",          // feedback
                "John Doe",                           // createdBy
                Instant.now(),                        // createdAt
                "Admin",                              // reviewedBy
                Instant.now()                         // reviewedAt
        );
        // Given
        ApprovalDecisionDto decisionDto = new ApprovalDecisionDto(Decision.APPROVED, "OK");
        Course course = new Course();
        course.setId("course1");
        approvalRequest.setCourse(course);

        when(approvalRequestRepository.findById(1)).thenReturn(Optional.of(approvalRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(approvalRequestRepository.save(any())).thenReturn(approvalRequest);
        when(approvalRequestMapper.toDto(any())).thenReturn(dto);

        // When
        ApprovalRequestDto result = approvalRequestService.processApproval(1, decisionDto, 2L);

        // Then
        assertThat(result).isNotNull();
        assertThat(approvalRequest.getDecision()).isEqualTo(Decision.APPROVED);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void processApproval_ShouldThrow_WhenRequestNotFound() {
        // Given
        when(approvalRequestRepository.findById(99)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() ->
                approvalRequestService.processApproval(99, new ApprovalDecisionDto(Decision.APPROVED, "OK"), 1L)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void processApproval_ShouldThrow_WhenDecisionInvalid() {
        // Given
        ApprovalDecisionDto decisionDto = new ApprovalDecisionDto(Decision.PENDING, "Invalid");

        when(approvalRequestRepository.findById(1)).thenReturn(Optional.of(approvalRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        // When / Then
        assertThatThrownBy(() ->
                approvalRequestService.processApproval(1, decisionDto, 2L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quyết định phải là APPROVED hoặc REJECTED");
    }
}
