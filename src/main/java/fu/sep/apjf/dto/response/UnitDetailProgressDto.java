package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record UnitDetailProgressDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String chapterId,
        String prerequisiteUnitId,
        boolean isCompleted
) {
}
