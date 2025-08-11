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

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "email", source = "post.user.email")
    @Mapping(target = "avatar", source = "post.user.avatar")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "commentsCount", source = "commentsCount")
    PostResponseDto toDto(Post post, Long commentsCount);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(PostRequestDto dto);
}
