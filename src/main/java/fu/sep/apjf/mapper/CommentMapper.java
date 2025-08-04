package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentRequestDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(comment.getId()))")
    @Mapping(target = "email", expression = "java(comment.getUser() != null ? comment.getUser().getEmail() : null)")
    @Mapping(target = "avatar", expression = "java(comment.getUser() != null ? comment.getUser().getAvatar() : null)")
    @Mapping(target = "postId", expression = "java(comment.getPost() != null ? String.valueOf(comment.getPost().getId()) : null)")
    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "id", expression = "java(comment.getId() != null ? String.valueOf(comment.getId()) : null)")
    @Mapping(target = "userId", expression = "java(comment.getUser() != null ? String.valueOf(comment.getUser().getId()) : null)")
    @Mapping(target = "postId", expression = "java(comment.getPost() != null ? String.valueOf(comment.getPost().getId()) : null)")
    CommentRequestDto toRequestDto(Comment comment);

    @Mapping(target = "id", expression = "java(dto.id() != null ? Long.parseLong(dto.id()) : null)")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "post", source = "post")
    Comment toEntity(CommentRequestDto dto, @Context User user, @Context Post post);
}
