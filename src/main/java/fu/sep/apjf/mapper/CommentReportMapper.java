package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentReportRequestDto;
import fu.sep.apjf.dto.response.CommentReportResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.CommentReport;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentReportMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(report.getId()))")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? String.valueOf(report.getUser().getId()) : null)")
    @Mapping(target = "commentId", expression = "java(report.getComment() != null ? String.valueOf(report.getComment().getId()) : null)")
    CommentReportResponseDto toDto(CommentReport report);

    @Mapping(target = "id", expression = "java(report.getId() != null ? String.valueOf(report.getId()) : null)")
    @Mapping(target = "userId", expression = "java(report.getUser() != null ? String.valueOf(report.getUser().getId()) : null)")
    @Mapping(target = "commentId", expression = "java(report.getComment() != null ? String.valueOf(report.getComment().getId()) : null)")
    CommentReportRequestDto toRequestDto(CommentReport report);

    @Mapping(target = "id", expression = "java(dto.id() != null ? Long.parseLong(dto.id()) : null)")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "comment", source = "comment")
    CommentReport toEntity(CommentReportRequestDto dto, @Context User user, @Context Comment comment);
}
