package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public final class PostMapper {

    private PostMapper() {}

    public static PostResponseDto toDto(Post post) {
        if (post == null) return null;

        List<CommentResponseDto> commentDtos = post.getComments().stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return new PostResponseDto(
                String.valueOf(post.getId()),
                post.getContent(),
                post.getCreatedAt(),
                post.getUser().getEmail(),
                post.getUser().getAvatar(),
                commentDtos
        );
    }

    public static PostRequestDto toRequestDto(Post post) {
        if (post == null) return null;

        return new PostRequestDto(
                String.valueOf(post.getId()),
                post.getContent()
        );
    }

    public static Post toEntity(PostRequestDto dto, User user) {
        if (dto == null) return null;

        Post post = new Post();
        post.setId(dto.id() != null ? Long.parseLong(dto.id()) : null);
        post.setContent(dto.content());
        post.setUser(user);
        return post;
    }
}
