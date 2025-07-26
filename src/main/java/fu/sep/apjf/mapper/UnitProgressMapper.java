package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.UnitProgressDto;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;

public class UnitProgressMapper {

    private UnitProgressMapper() {}

    public static UnitProgressDto toDto(UnitProgress entity) {
        return new UnitProgressDto(
                entity.getUnit().getId(),
                entity.getUser().getId(),
                entity.isPassed(),
                entity.getPassedAt()
        );
    }

    public static UnitProgress toEntity(UnitProgressDto dto, Unit unit, User user) {
        return UnitProgress.builder()
                .id(new UnitProgressKey(dto.unitId(), dto.userId()))
                .unit(unit)
                .user(user)
                .isPassed(dto.isPassed())
                .passedAt(dto.passedAt())
                .build();
    }
}
