package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostLikeRequestDto;
import fu.sep.apjf.dto.response.PostLikeResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostLike;
import fu.sep.apjf.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostLikeMapper {

    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "totalLikes", source = "totalLikes")
    PostLikeResponseDto toDto(boolean liked, int totalLikes);

     @Mapping(target = "user", ignore = true)
     @Mapping(target = "post", ignore = true)
     PostLike toEntity(PostLikeRequestDto dto);
}
