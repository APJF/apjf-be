package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper(componentModel = "spring", uses = {CommentMapper.class}, imports = {Objects.class})
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", expression = "java(String.valueOf(post.getId()))")
    @Mapping(target = "email", source = "post.user.email")
    @Mapping(target = "avatar", source = "post.user.avatar")
    @Mapping(target = "comments", source = "post.comments")
    @Mapping(target = "likeCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    @Mapping(target = "likedByCurrentUser", expression = "java(post.getLikes() != null && post.getLikes().stream().anyMatch(like -> Objects.equals(like.getUser().getId(), currentUserId)))")
    PostResponseDto toDto(Post post, @Context Long currentUserId);

    @Mapping(target = "id", expression = "java(dto.id() != null ? dto.id() : null)")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Post toEntity(PostRequestDto dto);
}
