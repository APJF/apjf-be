package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.UnitDetailProgressDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.UnitMapper;
import fu.sep.apjf.repository.ChapterRepository;
import fu.sep.apjf.repository.UnitProgressRepository;
import fu.sep.apjf.repository.UnitRepository;
import fu.sep.apjf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitServiceTest {

    @Mock
    private UnitRepository unitRepo;

    @Mock
    private ChapterRepository chapterRepo;

    @Mock
    private ApprovalRequestService approvalRequestService;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UnitProgressRepository unitProgressRepository;

    @InjectMocks
    private UnitService unitService;

    private Unit unit;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chapter = new Chapter();
        chapter.setId("CH1");

        unit = new Unit();
        unit.setId("U1");
        unit.setTitle("Unit 1");
        unit.setDescription("Description 1");
        unit.setChapter(chapter);
        unit.setStatus(EnumClass.Status.ACTIVE);
    }

    @Test
    void testList() {
        when(unitRepo.findAll()).thenReturn(List.of(unit));
        UnitResponseDto dto = new UnitResponseDto(unit.getId(), unit.getTitle(), unit.getDescription(), unit.getStatus(), unit.getChapter().getId(), null);
        when(unitMapper.toDto(unit)).thenReturn(dto);

        List<UnitResponseDto> result = unitService.list();
        assertEquals(1, result.size());
        assertEquals("U1", result.get(0).id());
    }

    @Test
    void testFindByChapterId_Success() {
        when(unitRepo.findByChapterId("CH1")).thenReturn(List.of(unit));
        UnitResponseDto dto = new UnitResponseDto(unit.getId(), unit.getTitle(), unit.getDescription(), unit.getStatus(), unit.getChapter().getId(), null);
        when(unitMapper.toDto(unit)).thenReturn(dto);

        List<UnitResponseDto> result = unitService.findByChapterId("CH1");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByChapterId_ThrowsException_WhenChapterNotFound() {
        when(unitRepo.findByChapterId("CH2")).thenReturn(Collections.emptyList());
        when(chapterRepo.existsById("CH2")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> unitService.findByChapterId("CH2"));
    }

    @Test
    void testFindById_Success() {
        when(unitRepo.findById("U1")).thenReturn(Optional.of(unit));
        UnitResponseDto dto = new UnitResponseDto(unit.getId(), unit.getTitle(), unit.getDescription(), unit.getStatus(), unit.getChapter().getId(), null);
        when(unitMapper.toDto(unit)).thenReturn(dto);

        UnitResponseDto result = unitService.findById("U1");
        assertEquals("U1", result.id());
    }

    @Test
    void testGetUnitDetailById_Success() {
        when(unitRepo.findById("U1")).thenReturn(Optional.of(unit));
        when(unitProgressRepository.findByUserIdAndUnitId(1L, "U1")).thenReturn(Optional.empty());

        UnitDetailProgressDto dto = unitService.getUnitDetailById("U1", 1L);
        assertEquals("U1", dto.id());
        assertFalse(dto.isCompleted());
    }

    @Test
    void testCreate_Success() {
        UnitRequestDto dto = new UnitRequestDto("U2", "New Unit", "Desc", EnumClass.Status.ACTIVE, "CH1",null);
        when(chapterRepo.findById("CH1")).thenReturn(Optional.of(chapter));
        when(unitRepo.existsById("U2")).thenReturn(false);

        Unit newUnit = new Unit();
        newUnit.setId("U2");
        newUnit.setChapter(chapter);
        when(unitMapper.toEntity(dto)).thenReturn(newUnit);
        when(unitRepo.save(newUnit)).thenReturn(newUnit);

        UnitResponseDto responseDto = new UnitResponseDto(newUnit.getId(), "New Unit", "Desc", EnumClass.Status.INACTIVE, "CH1", null);
        when(unitMapper.toDto(newUnit)).thenReturn(responseDto);

        UnitResponseDto result = unitService.create(dto, 1L);
        assertEquals("U2", result.id());
        verify(approvalRequestService).autoCreateApprovalRequest(ApprovalRequest.TargetType.UNIT, "U2", ApprovalRequest.RequestType.CREATE, 1L);
    }

    @Test
    void testUpdate_Success() {
        UnitRequestDto dto = new UnitRequestDto("U1", "Updated", "Desc Updated", EnumClass.Status.ACTIVE, "CH1", null);
        when(unitRepo.findById("U1")).thenReturn(Optional.of(unit));
        when(unitRepo.save(unit)).thenReturn(unit);

        UnitResponseDto responseDto = new UnitResponseDto("U1", "Updated", "Desc Updated", EnumClass.Status.INACTIVE, "CH1", null);
        when(unitMapper.toDto(unit)).thenReturn(responseDto);

        UnitResponseDto result = unitService.update("U1", dto, 1L);
        assertEquals("U1", result.id());
        assertEquals("Updated", result.title());
        verify(approvalRequestService).autoCreateApprovalRequest(ApprovalRequest.TargetType.UNIT, "U1", ApprovalRequest.RequestType.UPDATE, 1L);
    }

    @Test
    void testDeactivate_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(unitRepo.findById("U1")).thenReturn(Optional.of(unit));
        when(unitRepo.save(unit)).thenReturn(unit);

        UnitResponseDto responseDto = new UnitResponseDto("U1", "Unit 1", "Description 1", EnumClass.Status.INACTIVE, "CH1", null);
        when(unitMapper.toDto(unit)).thenReturn(responseDto);

        UnitResponseDto result = unitService.deactivate("U1", 1L);
        assertEquals(EnumClass.Status.INACTIVE, result.status());
    }

    @Test
    void testDeactivate_ThrowsException_WhenAlreadyInactive() {
        unit.setStatus(EnumClass.Status.INACTIVE);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(unitRepo.findById("U1")).thenReturn(Optional.of(unit));

        assertThrows(IllegalStateException.class, () -> unitService.deactivate("U1", 1L));
    }
}
