package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.entity.Chapter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ExamOverviewMapper.class})
public interface ChapterMapper {

    // Mặc định không load exams (cho findAll)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "prerequisiteChapterId", source = "prerequisiteChapter.id")
    // id, title, description, status tự động map
    ChapterResponseDto toDto(Chapter chapter);

    // Load cả exams (cho findById)
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "prerequisiteChapterId", source = "prerequisiteChapter.id")
    // exams, id, title, description, status tự động map
    ChapterResponseDto toDtoWithExams(Chapter chapter);

    // Entity mapping (giữ lại cho create/update)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteChapter", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    // id, title, description, status tự động map
    Chapter toEntity(ChapterRequestDto chapterDto);
}
