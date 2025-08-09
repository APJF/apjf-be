package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostReportRequestDto;
import fu.sep.apjf.dto.response.PostReportResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostReport;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PostReportMapper {

    @Mapping(target = "id", source = "post.user.id")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? report.getUser().getId() : null)")
    @Mapping(target = "postId", expression = "java(report.getPost() != null ? report.getPost().getId() : null)")
    PostReportResponseDto toDto(PostReport report);

    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "post", expression = "java(post)")
    @Mapping(target = "createdAt", ignore = true)
    PostReport toEntity(PostReportRequestDto dto, @Context User user, @Context Post post);
}
