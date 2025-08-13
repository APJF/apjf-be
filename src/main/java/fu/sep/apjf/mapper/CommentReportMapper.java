package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentReportRequestDto;
import fu.sep.apjf.dto.response.CommentReportResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.CommentReport;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentReportMapper {

    @Mapping(target = "id", expression = "java(report.getUser() != null ? report.getUser().getId() : null)")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? report.getUser().getId() : null)")
    @Mapping(target = "commentId", expression = "java(report.getComment() != null ? report.getComment().getId() : null)")
    CommentReportResponseDto toDto(CommentReport report);

    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "comment", expression = "java(comment)")
    @Mapping(target = "createdAt", ignore = true)
    CommentReport toEntity(CommentReportRequestDto dto, @Context User user, @Context Comment comment);
}
