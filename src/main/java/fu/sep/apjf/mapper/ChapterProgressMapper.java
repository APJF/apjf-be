package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ChapterProgressRequestDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.entity.ChapterProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UnitProgressMapper.class})
public interface ChapterProgressMapper {

    @Mapping(target = "chapterId", source = "id.chapterId")
    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "chapterTitle", source = "chapter.title")
    @Mapping(target = "unitProgresses", source = "unitProgresses")
    ChapterProgressResponseDto toDto(ChapterProgress entity);

    @Mapping(target = "id.chapterId", source = "chapterId")
    @Mapping(target = "id.userId", source = "userId")
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "unitProgresses", ignore = true)
    ChapterProgress toEntity(ChapterProgressRequestDto dto);
}