package fu.sep.apjf.repository;

import fu.sep.apjf.dto.response.NotificationResponseDto;
import fu.sep.apjf.entity.Notification;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);


    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);


    Long countByRecipientAndIsReadFalse(User recipient);

    void markAsRead(Long id);
}
