package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", expression = "java(String.valueOf(post.getId()))")
    @Mapping(target = "email", source = "post.user.email")
    @Mapping(target = "avatar", source = "post.user.avatar")
    @Mapping(target = "comments", source = "post.comments")
    @Mapping(target = "likeCount", expression = "java(post.getPostLikes() != null ? post.getPostLikes().size() : 0)")
    @Mapping(target = "liked", expression = "java(post.getPostLikes().stream().anyMatch(like -> Objects.equals(like.getUser().getId(), currentUserId)))")
    PostResponseDto toDto(Post post, @Context Long currentUserId);

    @Mapping(target = "id", expression = "java(dto.id() != null ? Long.parseLong(dto.id()) : null)")
    @Mapping(target = "user", ignore = true) // cần set bằng tay
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Post toEntity(PostRequestDto dto);
}
