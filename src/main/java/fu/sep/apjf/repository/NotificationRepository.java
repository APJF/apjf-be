package fu.sep.apjf.repository;

import fu.sep.apjf.dto.response.NotificationResponseDto;
import fu.sep.apjf.entity.Notification;
import fu.sep.apjf.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);


    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);


    Long countByRecipientAndIsReadFalse(User recipient);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
}
