package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.NotificationRequestDto;
import fu.sep.apjf.dto.response.NotificationResponseDto;
import fu.sep.apjf.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Mapping Notification -> NotificationResponseDto
    @Mapping(source = "read", target = "read")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "post.id", target = "postId")
    NotificationResponseDto toDto(Notification notification);


    // Mapping NotificationRequestDto -> Notification
    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "postId", target = "post.id")
    @Mapping(target = "recipient", ignore = true) // recipient sẽ được set thủ công trong service
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "isRead", constant = "false")
    Notification toEntity(NotificationRequestDto dto);
}