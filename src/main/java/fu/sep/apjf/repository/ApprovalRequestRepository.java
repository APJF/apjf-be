package fu.sep.apjf.repository;

import fu.sep.apjf.entity.*;
import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Integer> {
    List<ApprovalRequest> findByDecision(Decision decision);

    List<ApprovalRequest> findByTargetType(TargetType targetType);

    List<ApprovalRequest> findByRequestType(RequestType requestType);

    List<ApprovalRequest> findByCreator(User creator);

    List<ApprovalRequest> findByReviewer(User reviewer);

    List<ApprovalRequest> findByCourse(Course course);

    List<ApprovalRequest> findByChapter(Chapter chapter);

    List<ApprovalRequest> findByUnit(Unit unit);

    List<ApprovalRequest> findByMaterial(Material material);

    List<ApprovalRequest> findByDecisionAndTargetType(Decision decision, TargetType targetType);

    List<ApprovalRequest> findByDecisionAndCreator(Decision decision, User creator);

    List<ApprovalRequest> findByDecisionAndReviewer(Decision decision, User reviewer);

    List<ApprovalRequest> findByCreatedAtBetween(Instant startDate, Instant endDate);

    List<ApprovalRequest> findByOrderByCreatedAtDesc();

    List<ApprovalRequest> findByDecisionOrderByCreatedAtDesc(Decision decision);

    @Query("SELECT ar FROM ApprovalRequest ar WHERE " +
            "(ar.course.id = :targetId AND ar.targetType = 'COURSE') OR " +
            "(ar.chapter.id = :targetId AND ar.targetType = 'CHAPTER') OR " +
            "(ar.unit.id = :targetId AND ar.targetType = 'UNIT') OR " +
            "(ar.material.id = :targetId AND ar.targetType = 'MATERIAL') " +
            "ORDER BY ar.createdAt DESC")
    List<ApprovalRequest> findByTargetIdOrderByCreatedAtDesc(@Param("targetId") String targetId);

    @Query("SELECT ar FROM ApprovalRequest ar WHERE " +
            "ar.decision = 'PENDING' AND (" +
            "(ar.course.id = :targetId AND ar.targetType = 'COURSE') OR " +
            "(ar.chapter.id = :targetId AND ar.targetType = 'CHAPTER') OR " +
            "(ar.unit.id = :targetId AND ar.targetType = 'UNIT') OR " +
            "(ar.material.id = :targetId AND ar.targetType = 'MATERIAL'))")
    Optional<ApprovalRequest> findPendingRequestByTargetId(@Param("targetId") String targetId);
}
