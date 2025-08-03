package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    MaterialResponseDto toDto(Material material);

    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    Material toEntity(MaterialRequestDto materialDto);

    // Entity mapping với Unit
    default Material toEntity(MaterialRequestDto materialDto, Unit unit) {
        Material material = toEntity(materialDto);
        if (material != null && unit != null) {
            material.setUnit(unit);
        }
        return material;
    }
}
