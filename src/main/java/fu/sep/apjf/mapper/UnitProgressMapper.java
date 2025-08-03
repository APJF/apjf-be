package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.UnitProgressDto;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UnitProgressMapper {

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "userId", source = "user.id")
    // passed và passedAt tự động map (tên giống nhau)
    UnitProgressDto toDto(UnitProgress entity);

    // Entity mapping với composite key - PHẢI dùng custom method
    default UnitProgress toEntity(UnitProgressDto dto, Unit unit, User user) {
        if (dto == null || unit == null || user == null) {
            return null;
        }

        return UnitProgress.builder()
                .id(new UnitProgressKey(dto.unitId(), dto.userId()))
                .unit(unit)
                .user(user)
                .passed(dto.passed())
                .passedAt(dto.passedAt())
                .build();
    }
}
