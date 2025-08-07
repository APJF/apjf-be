package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ExamRequestDto;
import fu.sep.apjf.dto.response.ExamResponseDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "unitId", source = "unit.id")
    ExamResponseDto toDto(Exam exam);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "course", source = "courseId", qualifiedByName = "mapIdToCourse")
    @Mapping(target = "chapter", source = "chapterId", qualifiedByName = "mapIdToChapter")
    @Mapping(target = "unit", source = "unitId", qualifiedByName = "mapIdToUnit")
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "results", ignore = true)
    Exam toEntity(ExamRequestDto dto);

    @Named("mapIdToCourse")
    static Course mapIdToCourse(String id) {
        if (id == null || id.isBlank()) return null;
        Course c = new Course();
        c.setId(id);
        return c;
    }

    @Named("mapIdToChapter")
    static Chapter mapIdToChapter(String id) {
        if (id == null || id.isBlank()) return null;
        Chapter c = new Chapter();
        c.setId(id);
        return c;
    }

    @Named("mapIdToUnit")
    static Unit mapIdToUnit(String id) {
        if (id == null || id.isBlank()) return null;
        Unit u = new Unit();
        u.setId(id);
        return u;
    }

}


