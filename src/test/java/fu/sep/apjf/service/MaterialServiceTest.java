package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.MaterialMapper;
import fu.sep.apjf.repository.MaterialRepository;
import fu.sep.apjf.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private MaterialMapper materialMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private MaterialService materialService;

    private Material material;
    private Unit unit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        unit = new Unit();
        unit.setId("U1");

        material = new Material();
        material.setId("M1");
        material.setUnit(unit);
        material.setType(EnumClass.MaterialType.GRAMMAR);
        material.setFileUrl("file.pdf");
    }

    @Test
    void testFindAll() throws Exception {
        when(materialRepository.findAll()).thenReturn(List.of(material));

        MaterialResponseDto dto = new MaterialResponseDto("M1", "https://example.com/file.pdf", EnumClass.MaterialType.GRAMMAR, null, null);
        when(materialMapper.toDto(material)).thenReturn(dto);
        when(minioService.getDocumentUrl("file.pdf")).thenReturn("https://example.com/file.pdf");

        List<MaterialResponseDto> result = materialService.findAll();
        assertEquals(1, result.size());
        assertEquals("M1", result.get(0).id());
        assertEquals("https://example.com/file.pdf", result.get(0).fileUrl());
    }

    @Test
    void testFindByUnitId_Success() throws Exception {
        when(materialRepository.findByUnitId("U1")).thenReturn(List.of(material));
        MaterialResponseDto dto = new MaterialResponseDto("M1", "https://example.com/file.pdf", EnumClass.MaterialType.GRAMMAR, null, null);
        when(materialMapper.toDto(material)).thenReturn(dto);
        when(minioService.getDocumentUrl("file.pdf")).thenReturn("https://example.com/file.pdf");

        List<MaterialResponseDto> result = materialService.findByUnitId("U1");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUnitId_ThrowsException_WhenUnitNotFound() {
        when(materialRepository.findByUnitId("U2")).thenReturn(Collections.emptyList());
        when(unitRepository.existsById("U2")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> materialService.findByUnitId("U2"));
    }

    @Test
    void testFindById_Success() throws Exception {
        when(materialRepository.findById("M1")).thenReturn(Optional.of(material));
        MaterialResponseDto dto = new MaterialResponseDto("M1", "https://example.com/file.pdf", EnumClass.MaterialType.GRAMMAR, null, null);
        when(materialMapper.toDto(material)).thenReturn(dto);
        when(minioService.getDocumentUrl("file.pdf")).thenReturn("https://example.com/file.pdf");

        MaterialResponseDto result = materialService.findById("M1");
        assertEquals("M1", result.id());
        assertEquals("https://example.com/file.pdf", result.fileUrl());
    }

    @Test
    void testFindById_ThrowsException_WhenNotFound() {
        when(materialRepository.findById("M2")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> materialService.findById("M2"));
    }

    @Test
    void testCreate_Success() {
        // Tạo unit giả lập
        Unit unit = new Unit();
        unit.setId("U1");

        // DTO input
        MaterialRequestDto dto = new MaterialRequestDto(
                "M2",                       // id
                null,                       // fileUrl
                EnumClass.MaterialType.GRAMMAR, // type
                null,                       // script
                null,                       // translation
                "U1"                        // unitId
        );


        // Mock unitRepository
        when(unitRepository.findById("U1")).thenReturn(Optional.of(unit));

        // Material entity giả lập
        Material material = new Material();
        material.setId("M2");
        material.setUnit(unit);

        // Mock mapper và repository
        when(materialMapper.toEntity(dto, unit)).thenReturn(material);
        when(materialRepository.save(material)).thenReturn(material);

        // MaterialResponseDto giả lập
        MaterialResponseDto responseDto = new MaterialResponseDto(
                "M2",
                null,
                EnumClass.MaterialType.GRAMMAR,
                null,
                null
        );
        when(materialMapper.toDto(material)).thenReturn(responseDto);

        // Gọi service
        MaterialResponseDto result = materialService.create(dto, 1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("M2", result.id());
        assertEquals(EnumClass.MaterialType.GRAMMAR, result.type());
    }


    @Test
    void testUpdate_Success() {
        MaterialRequestDto dto = new MaterialRequestDto("M1", "U1", EnumClass.MaterialType.GRAMMAR, null, null,null);
        when(materialRepository.findById("M1")).thenReturn(Optional.of(material));
        when(unitRepository.findById("U1")).thenReturn(Optional.of(unit));

        Material updatedMaterial = new Material();
        updatedMaterial.setId("M1");
        updatedMaterial.setUnit(unit);

        when(materialMapper.toEntity(dto, unit)).thenReturn(updatedMaterial);
        when(materialRepository.save(updatedMaterial)).thenReturn(updatedMaterial);

        MaterialResponseDto responseDto = new MaterialResponseDto("M1", null, EnumClass.MaterialType.GRAMMAR, null, null);
        when(materialMapper.toDto(updatedMaterial)).thenReturn(responseDto);

        MaterialResponseDto result = materialService.update("M1", dto, "U1", 1L);
        assertEquals("M1", result.id());
    }

    @Test
    void testDelete_Success() {
        when(materialRepository.findById("M1")).thenReturn(Optional.of(material));
        doNothing().when(materialRepository).delete(material);

        assertDoesNotThrow(() -> materialService.delete("M1"));
        verify(materialRepository).delete(material);
    }

    @Test
    void testDelete_ThrowsException_WhenNotFound() {
        when(materialRepository.findById("M2")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> materialService.delete("M2"));
    }
}
