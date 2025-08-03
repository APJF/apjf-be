package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CommentReportRequestDto;
import fu.sep.apjf.dto.response.CommentReportResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.CommentReport;
import fu.sep.apjf.entity.User;

public final class CommentReportMapper {

    private CommentReportMapper() {}

    public static CommentReportResponseDto toDto(CommentReport report) {
        if (report == null) return null;

        return new CommentReportResponseDto(
                String.valueOf(report.getId()),
                report.getContent(),
                report.getCreatedAt(),
                report.getUser() != null ? String.valueOf(report.getUser().getId()) : null,
                report.getComment() != null ? String.valueOf(report.getComment().getId()) : null
        );
    }

    public static CommentReportRequestDto toRequestDto(CommentReport report) {
        if (report == null) return null;

        return new CommentReportRequestDto(
                String.valueOf(report.getId()),
                report.getContent(),
                report.getUser() != null ? String.valueOf(report.getUser().getId()) : null,
                report.getComment() != null ? String.valueOf(report.getComment().getId()) : null
        );
    }

    public static CommentReport toEntity(CommentReportRequestDto dto, User user, Comment comment) {
        if (dto == null) return null;

        CommentReport report = new CommentReport();
        report.setId(dto.id() != null ? Long.parseLong(dto.id()) : null);
        report.setContent(dto.content());
        report.setUser(user);
        report.setComment(comment);
        return report;
    }
}
