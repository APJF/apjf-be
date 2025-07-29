package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;

public final class MaterialMapper {

    private MaterialMapper() {
        // Private constructor to prevent instantiation
    }

    public static MaterialResponseDto toResponseDto(Material material) {
        if (material == null) {
            return null;
        }

        return new MaterialResponseDto(
                material.getId(),
                material.getDescription(),
                material.getFileUrl(),
                material.getType()
        );
    }

    public static Material toEntity(MaterialRequestDto materialDto) {
        if (materialDto == null) {
            return null;
        }

        Material material = new Material();
        material.setId(materialDto.id());
        material.setDescription(materialDto.description());
        material.setFileUrl(materialDto.fileUrl());
        material.setType(materialDto.type());

        return material;
    }

    public static Material toEntity(MaterialRequestDto materialDto, Unit unit) {
        Material material = toEntity(materialDto);
        if (material != null && unit != null) {
            material.setUnit(unit);
        }
        return material;
    }
}
