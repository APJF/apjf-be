package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.dto.response.UnitDetailResponseDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.Unit;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ExamOverviewMapper.class})
public interface UnitMapper {

    // Mặc định không load exams (cho findAll)
    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "prerequisiteUnitId", source = "prerequisiteUnit.id")
    // id, title, description, status tự động map
    UnitResponseDto toDto(Unit unit);

    @Mapping(target = "chapterId", source = "unit.chapter.id")
    @Mapping(target = "prerequisiteUnitId", source = "unit.prerequisiteUnit.id")
    @Mapping(target = "materials", source = "materials")
    UnitDetailResponseDto toDetailDto(Unit unit, List<MaterialResponseDto> materials);

    // Entity mapping (giữ lại cho create/update)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "prerequisiteUnit", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "materials", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "unitProgresses", ignore = true)
    // id, title, description, status tự động map
    Unit toEntity(UnitRequestDto unitDto);
}
