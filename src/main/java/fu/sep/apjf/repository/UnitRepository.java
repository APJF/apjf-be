package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    // ========== BASIC FINDER METHODS (Using naming convention) ==========

    /**
     * Find units by chapter
     */
    List<Unit> findByChapter(Chapter chapter);

    /**
     * Find units by status
     */
    List<Unit> findByStatus(EnumClass.Status status);

    /**
     * Find units by prerequisite unit
     */
    List<Unit> findByPrerequisiteUnit(Unit prerequisiteUnit);

    /**
     * Find units without prerequisite (entry level units)
     */
    List<Unit> findByPrerequisiteUnitIsNull();

    // ========== TEXT SEARCH ==========

    /**
     * Find units by title containing text (case insensitive)
     */
    List<Unit> findByTitleContainingIgnoreCase(String title);

    /**
     * Find units by description containing text (case insensitive)
     */
    List<Unit> findByDescriptionContainingIgnoreCase(String description);

    // ========== COMBINED CRITERIA ==========

    /**
     * Find units by chapter and status
     */
    List<Unit> findByChapterAndStatus(Chapter chapter, EnumClass.Status status);

    /**
     * Find units by status and no prerequisite
     */
    List<Unit> findByStatusAndPrerequisiteUnitIsNull(EnumClass.Status status);

    // ========== ORDERING QUERIES ==========

    /**
     * Find units by chapter ordered by title
     */
    List<Unit> findByChapterOrderByTitleAsc(Chapter chapter);

    /**
     * Find units by status ordered by title
     */
    List<Unit> findByStatusOrderByTitleAsc(EnumClass.Status status);

    // ========== COUNTING METHODS ==========

    /**
     * Count units by chapter
     */
    long countByChapter(Chapter chapter);

    /**
     * Count units by status
     */
    long countByStatus(EnumClass.Status status);

    /**
     * Count units by chapter and status
     */
    long countByChapterAndStatus(Chapter chapter, EnumClass.Status status);

    // ========== EXISTENCE CHECKS ==========

    /**
     * Check if units exist for specific chapter
     */
    boolean existsByChapter(Chapter chapter);

    /**
     * Check if unit exists by title in chapter (case insensitive)
     */
    boolean existsByChapterAndTitleIgnoreCase(Chapter chapter, String title);

    /**
     * Check if any unit has specific prerequisite
     */
    boolean existsByPrerequisiteUnit(Unit prerequisiteUnit);
}