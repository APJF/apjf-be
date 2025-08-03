package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.PostReportRequestDto;
import fu.sep.apjf.dto.response.PostReportResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostReport;
import fu.sep.apjf.entity.User;

public final class PostReportMapper {

    private PostReportMapper() {}

    public static PostReportResponseDto toDto(PostReport report) {
        if (report == null) return null;

        return new PostReportResponseDto(
                String.valueOf(report.getId()),
                report.getContent(),
                report.getCreatedAt(),
                report.getUser() != null ? String.valueOf(report.getUser().getId()) : null,
                report.getPost() != null ? String.valueOf(report.getPost().getId()) : null
        );
    }

    public static PostReportRequestDto toRequestDto(PostReport report) {
        if (report == null) return null;

        return new PostReportRequestDto(
                String.valueOf(report.getId()),
                report.getContent(),
                report.getUser() != null ? String.valueOf(report.getUser().getId()) : null,
                report.getPost() != null ? String.valueOf(report.getPost().getId()) : null
        );
    }

    public static PostReport toEntity(PostReportRequestDto dto, User user, Post post) {
        if (dto == null) return null;

        PostReport report = new PostReport();
        report.setId(dto.id() != null ? Long.parseLong(dto.id()) : null);
        report.setContent(dto.content());
        report.setUser(user);
        report.setPost(post);
        return report;
    }
}
