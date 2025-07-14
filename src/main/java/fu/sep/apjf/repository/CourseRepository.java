package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    // ========== OPTIMIZED FINDER METHODS (With EntityGraph) ==========

    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Optional<Course> findById(String id);

    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Page<Course> findAll(Pageable pageable);

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find courses by status
     */
    List<Course> findByStatus(EnumClass.Status status);

    /**
     * Find courses by level
     */
    List<Course> findByLevel(EnumClass.Level level);

    /**
     * Find courses by prerequisite course
     */
    List<Course> findByPrerequisiteCourse(Course prerequisiteCourse);

    /**
     * Find courses that don't have prerequisite (entry level courses)
     */
    List<Course> findByPrerequisiteCourseIsNull();

    // ========== TEXT SEARCH ==========

    /**
     * Find courses by title containing text (case insensitive)
     */
    List<Course> findByTitleContainingIgnoreCase(String title);

    /**
     * Find courses by description containing text (case insensitive)
     */
    List<Course> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find courses by title starting with text
     */
    List<Course> findByTitleStartingWithIgnoreCase(String titlePrefix);

    // ========== DURATION-BASED QUERIES ==========

    /**
     * Find courses with duration greater than or equal to specific value
     */
    List<Course> findByDurationGreaterThanEqual(BigDecimal minDuration);

    /**
     * Find courses with duration less than or equal to specific value
     */
    List<Course> findByDurationLessThanEqual(BigDecimal maxDuration);

    /**
     * Find courses with duration between two values
     */
    List<Course> findByDurationBetween(BigDecimal minDuration, BigDecimal maxDuration);

    // ========== COMBINED CRITERIA ==========

    /**
     * Find courses by status and level
     */
    List<Course> findByStatusAndLevel(EnumClass.Status status, EnumClass.Level level);

    /**
     * Find courses by level and no prerequisite
     */
    List<Course> findByLevelAndPrerequisiteCourseIsNull(EnumClass.Level level);


    // ========== ORDERING QUERIES ==========

    /**
     * Find courses by status ordered by duration (shortest first)
     */
    List<Course> findByStatusOrderByDurationAsc(EnumClass.Status status);

    /**
     * Find courses by level ordered by title
     */
    List<Course> findByLevelOrderByTitleAsc(EnumClass.Level level);

    /**
     * Find all courses ordered by level and title
     */
    List<Course> findByOrderByLevelAscTitleAsc();

    // ========== PAGINATION WITH CRITERIA ==========

    /**
     * Find courses by status with pagination
     */
    Page<Course> findByStatus(EnumClass.Status status, Pageable pageable);

    /**
     * Find courses by level with pagination
     */
    Page<Course> findByLevel(EnumClass.Level level, Pageable pageable);

    /**
     * Find courses by title containing text with pagination
     */
    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // ========== COUNTING METHODS ==========

    /**
     * Count courses by status
     */
    long countByStatus(EnumClass.Status status);

    /**
     * Count courses by level
     */
    long countByLevel(EnumClass.Level level);

    /**
     * Count courses with prerequisite
     */
    long countByPrerequisiteCourseIsNotNull();

    /**
     * Count courses without prerequisite
     */
    long countByPrerequisiteCourseIsNull();

    // ========== EXISTENCE CHECKS ==========

    /**
     * Check if course exists by title (case insensitive)
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Check if any course has specific prerequisite
     */
    boolean existsByPrerequisiteCourse(Course prerequisiteCourse);

    /**
     * Check if published courses exist for specific level
     */
    boolean existsByStatusAndLevel(EnumClass.Status status, EnumClass.Level level);
}