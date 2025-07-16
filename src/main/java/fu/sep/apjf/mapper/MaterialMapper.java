package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.MaterialDto;
import fu.sep.apjf.entity.Material;
import fu.sep.apjf.entity.Unit;

public final class MaterialMapper {

    private MaterialMapper() {
        // Private constructor to prevent instantiation
    }

    public static MaterialDto toDto(Material material) {
        if (material == null) {
            return null;
        }

        String unitId = null;
        if (material.getUnit() != null) {
            unitId = material.getUnit().getId();
        }

        return new MaterialDto(
                material.getId(),
                material.getDescription(),
                material.getFileUrl(),
                material.getType(),
                unitId
        );
    }

    public static Material toEntity(MaterialDto materialDto) {
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

    public static Material toEntity(MaterialDto materialDto, Unit unit) {
        Material material = toEntity(materialDto);
        if (material != null && unit != null) {
            material.setUnit(unit);
        }
        return material;
    }
}

