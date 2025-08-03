package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.ApprovalRequestDto;
import fu.sep.apjf.entity.ApprovalRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalRequestMapper {

    @Mapping(target = "targetId", source = ".", qualifiedByName = "getTargetId")
    @Mapping(target = "createdBy", source = "creator.username")
    @Mapping(target = "reviewedBy", source = "reviewer.username")
    ApprovalRequestDto toDto(ApprovalRequest request);

    List<ApprovalRequestDto> toDtoList(List<ApprovalRequest> requests);

    @Named("getTargetId")
    default String getTargetId(ApprovalRequest request) {
        if (request == null) return null;

        return switch (request.getTargetType()) {
            case COURSE -> request.getCourse() != null ? request.getCourse().getId() : null;
            case CHAPTER -> request.getChapter() != null ? request.getChapter().getId() : null;
            case UNIT -> request.getUnit() != null ? request.getUnit().getId() : null;
            case MATERIAL -> request.getMaterial() != null ? request.getMaterial().getId() : null;
            default -> null;
        };
    }
}
