package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.NotificationRequestDto;
import fu.sep.apjf.dto.response.NotificationResponseDto;
import fu.sep.apjf.entity.Notification;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.NotificationMapper;
import fu.sep.apjf.repository.NotificationRepository;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepo;
    private final PostRepository postRepo;

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> list() {
        return notificationRepo.findAll()
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto get(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification không tồn tại"));
        return notificationMapper.toDto(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        return notificationRepo.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo"));

        notification.setRead(true);
        notificationRepo.save(notification);
    }




    public NotificationResponseDto create(@Valid NotificationRequestDto dto) {
        User sender = userRepo.findById(dto.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender không tồn tại"));

        Post post = postRepo.findById(dto.postId())
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        User recipient = post.getUser(); // lấy người nhận từ bài viết

        if (sender.getId().equals(recipient.getId())) {
            log.info("Sender và recipient giống nhau, không tạo notification.");
            return null; // hoặc ném exception nếu muốn
        }

        Notification notification = notificationMapper.toEntity(dto);
        notification.setSender(sender);
        notification.setPost(post);
        notification.setRecipient(recipient);

        Notification saved = notificationRepo.save(notification);
        return notificationMapper.toDto(saved);
    }

    public NotificationResponseDto update(Long id, @Valid NotificationRequestDto dto) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification không tồn tại"));

        // chỉ cho phép cập nhật nội dung và type
        notification.setContent(dto.content());

        Notification saved = notificationRepo.save(notification);
        return notificationMapper.toDto(saved);
    }

    public void delete(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification không tồn tại"));
        notificationRepo.delete(notification);
    }
}
