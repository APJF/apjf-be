package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentRequestDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;

public final class CommentMapper {

    private CommentMapper() {}

    public static CommentResponseDto toDto(Comment comment) {
        if (comment == null) return null;

        return new CommentResponseDto(
                String.valueOf(comment.getId()),
                comment.getContent(),
                comment.getUser() != null ? String.valueOf(comment.getUser().getId()) : null,
                comment.getPost() != null ? String.valueOf(comment.getPost().getId()) : null
        );
    }

    public static CommentRequestDto toRequestDto(Comment comment) {
        if (comment == null) return null;

        return new CommentRequestDto(
                String.valueOf(comment.getId()),
                comment.getContent(),
                comment.getUser() != null ? String.valueOf(comment.getUser().getId()) : null,
                comment.getPost() != null ? String.valueOf(comment.getPost().getId()) : null
        );
    }

    public static Comment toEntity(CommentRequestDto dto, User user, Post post) {
        if (dto == null) return null;

        Comment comment = new Comment();
        comment.setId(dto.id() != null ? Long.parseLong(dto.id()) : null);
        comment.setContent(dto.content());
        comment.setUser(user);
        comment.setPost(post);
        return comment;
    }
}
