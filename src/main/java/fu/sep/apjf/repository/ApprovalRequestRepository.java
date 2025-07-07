package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ApprovalRequest;
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

/**
 * Repository for ApprovalRequest entity
 * Provides query methods for filtering approval requests by various criteria
 */
@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Integer> {

    /**
     * Find approval requests by decision status
     */
    List<ApprovalRequest> findByDecision(Decision decision);

    /**
     * Find approval requests by target type
     */
    List<ApprovalRequest> findByTargetType(TargetType targetType);

    /**
     * Find approval requests by request type
     */
    List<ApprovalRequest> findByRequestType(RequestType requestType);

    /**
     * Find approval requests created by specific staff member
     */
    List<ApprovalRequest> findByCreatedBy(String createdBy);

    /**
     * Find approval requests reviewed by specific manager
     */
    List<ApprovalRequest> findByReviewedBy(String reviewedBy);

    /**
     * Find approval requests by decision and target type
     */
    List<ApprovalRequest> findByDecisionAndTargetType(Decision decision, TargetType targetType);

    /**
     * Find approval requests by decision and request type
     */
    List<ApprovalRequest> findByDecisionAndRequestType(Decision decision, RequestType requestType);

    /**
     * Find approval requests created within date range
     */
    List<ApprovalRequest> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find approval requests reviewed within date range
     */
    List<ApprovalRequest> findByReviewedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find approval requests for specific course
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE ar.course.id = :courseId")
    List<ApprovalRequest> findByCourseId(@Param("courseId") String courseId);

    /**
     * Find approval requests for specific chapter
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE ar.chapter.id = :chapterId")
    List<ApprovalRequest> findByChapterId(@Param("chapterId") String chapterId);

    /**
     * Find approval requests for specific unit
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE ar.unit.id = :unitId")
    List<ApprovalRequest> findByUnitId(@Param("unitId") String unitId);

    /**
     * Find approval requests for specific material
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE ar.material.id = :materialId")
    List<ApprovalRequest> findByMaterialId(@Param("materialId") String materialId);

    /**
     * Count pending approval requests
     */
    long countByDecision(Decision decision);

    /**
     * Count approval requests by manager
     */
    long countByReviewedBy(String managerId);

    /**
     * Count approval requests by staff
     */
    long countByCreatedBy(String staffId);

    /**
     * Find most recent approval request for a specific target
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE " +
           "(ar.course.id = :targetId AND ar.targetType = 'COURSE') OR " +
           "(ar.chapter.id = :targetId AND ar.targetType = 'CHAPTER') OR " +
           "(ar.unit.id = :targetId AND ar.targetType = 'UNIT') OR " +
           "(ar.material.id = :targetId AND ar.targetType = 'MATERIAL') " +
           "ORDER BY ar.createdAt DESC")
    List<ApprovalRequest> findByTargetIdOrderByCreatedAtDesc(@Param("targetId") String targetId);

    /**
     * Check if there's already a pending approval request for a specific target
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE " +
           "ar.decision = 'PENDING' AND (" +
           "(ar.course.id = :targetId AND ar.targetType = 'COURSE') OR " +
           "(ar.chapter.id = :targetId AND ar.targetType = 'CHAPTER') OR " +
           "(ar.unit.id = :targetId AND ar.targetType = 'UNIT') OR " +
           "(ar.material.id = :targetId AND ar.targetType = 'MATERIAL'))")
    Optional<ApprovalRequest> findPendingRequestByTargetId(@Param("targetId") String targetId);
}
