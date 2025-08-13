package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentRequestDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "username", expression = "java(comment.getUser() != null ? comment.getUser().getUsername() : null)")
    @Mapping(target = "email", expression = "java(comment.getUser() != null ? comment.getUser().getEmail() : null)")
    @Mapping(target = "avatar", expression = "java(comment.getUser() != null ? comment.getUser().getAvatar() : null)")
    @Mapping(target = "postId", expression = "java(comment.getPost() != null ? comment.getPost().getId() : null)")
    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "post", expression = "java(post)")
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentRequestDto dto, @Context User user, @Context Post post);
}
