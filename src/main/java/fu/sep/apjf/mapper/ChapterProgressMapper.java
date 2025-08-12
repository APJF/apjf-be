package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ChapterProgressRequestDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.ChapterProgress;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UnitProgressMapper.class})
public interface ChapterProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", source = "chapter")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "completed", source = "dto.completed")
    @Mapping(target = "completedAt", expression = "java(dto.completed() ? java.time.LocalDateTime.now() : null)")
    ChapterProgress toEntity(ChapterProgressRequestDto dto, Chapter chapter, User user);

    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "chapterTitle", source = "chapter.title")
    ChapterProgressResponseDto toResponseDto(ChapterProgress entity);
}
