package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ApprovalRequest entity
 * Uses Spring Data JPA naming conventions for automatic query generation
 */
@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Integer> {

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

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
     * Find approval requests created by specific user
     */
    List<ApprovalRequest> findByCreator(User creator);

    /**
     * Find approval requests reviewed by specific user
     */
    List<ApprovalRequest> findByReviewer(User reviewer);

    /**
     * Find approval requests for specific course
     */
    List<ApprovalRequest> findByCourse(Course course);

    /**
     * Find approval requests for specific chapter
     */
    List<ApprovalRequest> findByChapter(Chapter chapter);

    /**
     * Find approval requests for specific unit
     */
    List<ApprovalRequest> findByUnit(Unit unit);

    /**
     * Find approval requests for specific material
     */
    List<ApprovalRequest> findByMaterial(Material material);

    // ========== COMBINED CRITERIA (Using naming convention) ==========

    /**
     * Find approval requests by decision and target type
     */
    List<ApprovalRequest> findByDecisionAndTargetType(Decision decision, TargetType targetType);

    /**
     * Find approval requests by decision and request type
     */
    List<ApprovalRequest> findByDecisionAndRequestType(Decision decision, RequestType requestType);

    /**
     * Find approval requests by target type and creator
     */
    List<ApprovalRequest> findByTargetTypeAndCreator(TargetType targetType, User creator);

    /**
     * Find approval requests by decision and creator
     */
    List<ApprovalRequest> findByDecisionAndCreator(Decision decision, User creator);

    /**
     * Find approval requests by decision and reviewer
     */
    List<ApprovalRequest> findByDecisionAndReviewer(Decision decision, User reviewer);

    // ========== DATE RANGE QUERIES (Using naming convention) ==========

    /**
     * Find approval requests created within date range
     */
    List<ApprovalRequest> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find approval requests reviewed within date range
     */
    List<ApprovalRequest> findByReviewedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find approval requests created after specific date
     */
    List<ApprovalRequest> findByCreatedAtAfter(Instant date);

    /**
     * Find approval requests reviewed before specific date
     */
    List<ApprovalRequest> findByReviewedAtBefore(Instant date);

    // ========== COUNTING METHODS (Using naming convention) ==========

    /**
     * Count approval requests by decision
     */
    long countByDecision(Decision decision);

    /**
     * Count approval requests by target type
     */
    long countByTargetType(TargetType targetType);

    /**
     * Count approval requests by creator
     */
    long countByCreator(User creator);

    /**
     * Count approval requests by reviewer
     */
    long countByReviewer(User reviewer);

    /**
     * Count approval requests by decision and target type
     */
    long countByDecisionAndTargetType(Decision decision, TargetType targetType);

    // ========== EXISTENCE CHECKS (Using naming convention) ==========

    /**
     * Check if approval request exists for specific course
     */
    boolean existsByCourse(Course course);

    /**
     * Check if approval request exists for specific chapter
     */
    boolean existsByChapter(Chapter chapter);

    /**
     * Check if approval request exists for specific unit
     */
    boolean existsByUnit(Unit unit);

    /**
     * Check if approval request exists for specific material
     */
    boolean existsByMaterial(Material material);

    /**
     * Check if pending approval request exists for specific course
     */
    boolean existsByCourseAndDecision(Course course, Decision decision);

    /**
     * Check if pending approval request exists for specific chapter
     */
    boolean existsByChapterAndDecision(Chapter chapter, Decision decision);

    /**
     * Check if pending approval request exists for specific unit
     */
    boolean existsByUnitAndDecision(Unit unit, Decision decision);

    /**
     * Check if pending approval request exists for specific material
     */
    boolean existsByMaterialAndDecision(Material material, Decision decision);

    // ========== ORDERING QUERIES (Using naming convention) ==========

    /**
     * Find approval requests ordered by creation date (newest first)
     */
    List<ApprovalRequest> findByOrderByCreatedAtDesc();

    /**
     * Find approval requests by decision ordered by creation date
     */
    List<ApprovalRequest> findByDecisionOrderByCreatedAtDesc(Decision decision);

    /**
     * Find approval requests by creator ordered by creation date
     */
    List<ApprovalRequest> findByCreatorOrderByCreatedAtDesc(User creator);

    /**
     * Find approval requests by reviewer ordered by reviewed date
     */
    List<ApprovalRequest> findByReviewerOrderByReviewedAtDesc(User reviewer);

    // ========== SPECIFIC BUSINESS LOGIC (Custom queries only when necessary) ==========

    /**
     * Find most recent approval request for a specific target
     * Note: This requires custom query due to complex OR conditions across multiple entities
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
     * Note: This requires custom query due to complex OR conditions across multiple entities
     */
    @Query("SELECT ar FROM ApprovalRequest ar WHERE " +
           "ar.decision = 'PENDING' AND (" +
           "(ar.course.id = :targetId AND ar.targetType = 'COURSE') OR " +
           "(ar.chapter.id = :targetId AND ar.targetType = 'CHAPTER') OR " +
           "(ar.unit.id = :targetId AND ar.targetType = 'UNIT') OR " +
           "(ar.material.id = :targetId AND ar.targetType = 'MATERIAL'))")
    Optional<ApprovalRequest> findPendingRequestByTargetId(@Param("targetId") String targetId);
}
