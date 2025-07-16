package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ChapterDto;
import fu.sep.apjf.dto.CourseDetailDto;
import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.entity.Course;

import java.util.Set;
import java.util.stream.Collectors;

public final class CourseDetailMapper {
    private CourseDetailMapper() {
    }

    public static CourseDetailDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        CourseDto courseDto = CourseMapper.toDto(course);

        Set<ChapterDto> chapterDtos = course.getChapters().stream()
                .map(ChapterMapper::toDto)
                .collect(Collectors.toSet());

        return new CourseDetailDto(courseDto, chapterDtos);
    }
}
