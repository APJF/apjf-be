package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.dto.response.QuestionWithOptionsResponseDto;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.entity.Unit;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {OptionMapper.class})
public interface QuestionMapper {

    @Mapping(target = "options", source = "options")
    @Mapping(target = "unitIds", source = "units", qualifiedByName = "mapUnitsToIds")
    QuestionResponseDto toDto(Question question);

    @Mapping(target = "options", source = "options")
    QuestionWithOptionsResponseDto toWithOptionDto(Question question);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "examResultDetails", ignore = true)
    @Mapping(target = "units", source = "unitIds", qualifiedByName = "mapIdsToUnits")
    Question toEntity(QuestionRequestDto dto);

    @Named("mapUnitsToIds")
    static List<String> mapUnitsToIds(Set<Unit> units) {
        if (units == null || units.isEmpty()) return List.of();
        return units.stream()
                .map(Unit::getId)
                .toList(); // Java 16+
    }

    @Named("mapIdsToUnits")
    static Set<Unit> mapIdsToUnits(List<String> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) return Set.of();
        return unitIds.stream()
                .map(id -> {
                    Unit unit = new Unit();
                    unit.setId(id);
                    return unit;
                })
                .collect(Collectors.toSet());
    }

}