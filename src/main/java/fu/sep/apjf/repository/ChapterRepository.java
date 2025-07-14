package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find chapters by course
     */
    List<Chapter> findByCourse(Course course);

    /**
     * Find chapters by status
     */
    List<Chapter> findByStatus(EnumClass.Status status);

    /**
     * Find chapters by prerequisite chapter
     */
    List<Chapter> findByPrerequisiteChapter(Chapter prerequisiteChapter);

    /**
     * Find chapters without prerequisite (entry level chapters)
     */
    List<Chapter> findByPrerequisiteChapterIsNull();

    // ========== TEXT SEARCH ==========

    /**
     * Find chapters by title containing text (case insensitive)
     */
    List<Chapter> findByTitleContainingIgnoreCase(String title);

    /**
     * Find chapters by description containing text (case insensitive)
     */
    List<Chapter> findByDescriptionContainingIgnoreCase(String description);

    // ========== COMBINED CRITERIA ==========

    /**
     * Find chapters by course and status
     */
    List<Chapter> findByCourseAndStatus(Course course, EnumClass.Status status);

    /**
     * Find chapters by status and no prerequisite
     */
    List<Chapter> findByStatusAndPrerequisiteChapterIsNull(EnumClass.Status status);

    // ========== ORDERING QUERIES ==========

    /**
     * Find chapters by course ordered by title
     */
    List<Chapter> findByCourseOrderByTitleAsc(Course course);

    /**
     * Find chapters by status ordered by title
     */
    List<Chapter> findByStatusOrderByTitleAsc(EnumClass.Status status);

    // ========== COUNTING METHODS ==========

    /**
     * Count chapters by course
     */
    long countByCourse(Course course);

    /**
     * Count chapters by status
     */
    long countByStatus(EnumClass.Status status);

    /**
     * Count chapters by course and status
     */
    long countByCourseAndStatus(Course course, EnumClass.Status status);

    // ========== EXISTENCE CHECKS ==========

    /**
     * Check if chapters exist for specific course
     */
    boolean existsByCourse(Course course);

    /**
     * Check if chapter exists by title in course (case insensitive)
     */
    boolean existsByCourseAndTitleIgnoreCase(Course course, String title);

    /**
     * Check if any chapter has specific prerequisite
     */
    boolean existsByPrerequisiteChapter(Chapter prerequisiteChapter);
}