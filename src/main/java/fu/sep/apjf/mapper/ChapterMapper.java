package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterDetailResponseDto;
import fu.sep.apjf.dto.response.ChapterDetailWithProgressResponseDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.dto.response.UnitDetailResponseDto;
import fu.sep.apjf.entity.Chapter;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ExamOverviewMapper.class})
public interface ChapterMapper {

    // Mặc định không load exams (cho findAll)

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "prerequisiteChapterId", source = "prerequisiteChapter.id")
    // id, title, description, status tự động map
    ChapterResponseDto toDto(Chapter chapter);

    @Mapping(target = "courseId", source = "chapter.course.id")
    @Mapping(target = "prerequisiteChapterId", source = "chapter.prerequisiteChapter.id")
    @Mapping(target = "units", source = "units")
    ChapterDetailResponseDto toDetailDto(Chapter chapter, List<UnitDetailResponseDto> units);

    // Entity mapping (giữ lại cho create/update)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteChapter", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    // id, title, description, status tự động map
    Chapter toEntity(ChapterRequestDto chapterDto);
}
