package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ReviewRequestDto;
import fu.sep.apjf.dto.response.ReviewResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", source = "course")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    Review toEntity(ReviewRequestDto dto, Course course, User user);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "courseId", source = "course.id")
    ReviewResponseDto toDto(Review review);

    // Map từ entity User sang nested UserSummaryDto
    default ReviewResponseDto.UserSummaryDto mapUser(User user) {
        if (user == null) return null;
        return new ReviewResponseDto.UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getAvatar()
        );
    }
}
