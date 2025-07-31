package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;

public final class PostMapper {

    private PostMapper() {}

    public static PostResponseDto toDto(Post post) {
        if (post == null) return null;

        return new PostResponseDto(
                String.valueOf(post.getId()),
                post.getTitle(),
                post.getContent(),
                post.getUser() != null ? String.valueOf(post.getUser().getId()) : null
        );
    }

    public static PostRequestDto toRequestDto(Post post) {
        if (post == null) return null;

        return new PostRequestDto(
                String.valueOf(post.getId()),
                post.getTitle(),
                post.getContent(),
                post.getUser() != null ? String.valueOf(post.getUser().getId()) : null
        );
    }

    public static Post toEntity(PostRequestDto dto, User user) {
        if (dto == null) return null;

        Post post = new Post();
        post.setId(dto.id() != null ? Long.parseLong(dto.id()) : null);
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setUser(user);
        return post;
    }
}
