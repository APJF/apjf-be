package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostReportRequestDto;
import fu.sep.apjf.dto.response.PostReportResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostReport;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PostReportMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(report.getId()))")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? String.valueOf(report.getUser().getId()) : null)")
    @Mapping(target = "postId", expression = "java(report.getPost() != null ? String.valueOf(report.getPost().getId()) : null)")
    PostReportResponseDto toDto(PostReport report);

    @Mapping(target = "id", expression = "java(report.getId() != null ? String.valueOf(report.getId()) : null)")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? String.valueOf(report.getUser().getId()) : null)")
    @Mapping(target = "postId", expression = "java(report.getPost() != null ? String.valueOf(report.getPost().getId()) : null)")
    PostReportRequestDto toRequestDto(PostReport report);

    @Mapping(target = "id", expression = "java(dto.id() != null ? Long.parseLong(dto.id()) : null)")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "post", source = "post")
    PostReport toEntity(PostReportRequestDto dto, @Context User user, @Context Post post);
}
