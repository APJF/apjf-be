package fu.sep.apjf.repository;

import fu.sep.apjf.entity.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String>, JpaSpecificationExecutor<Exam> {
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions q LEFT JOIN FETCH q.options WHERE e.id = :id")
    Optional<Exam> findByIdWithQuestionsAndOptions(@Param("id") String id);

    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions WHERE e.id = :id")
    Optional<Exam> findByIdWithQuestions(@Param("id") String id);

    @NotNull
    @EntityGraph(attributePaths = {"questions"})
    @Override
    List<Exam> findAll();

    @NotNull
    @EntityGraph(attributePaths = {"questions"})
    @Override
    Optional<Exam> findById(@NotNull String id);

    List<Exam> findByExamScopeType(EnumClass.ExamScopeType scopeType);

    // Thêm phương thức phân trang
    Page<Exam> findByExamScopeType(EnumClass.ExamScopeType scopeType, Pageable pageable);

    List<Exam> findByCourse(Course course);

    // Thêm phương thức phân trang
    Page<Exam> findByCourse(Course course, Pageable pageable);

    List<Exam> findByChapter(Chapter chapter);

    // Thêm phương thức phân trang
    Page<Exam> findByChapter(Chapter chapter, Pageable pageable);

    List<Exam> findByUnit(Unit unit);

    // Thêm phương thức phân trang
    Page<Exam> findByUnit(Unit unit, Pageable pageable);

    List<Exam> findByTitleContainingIgnoreCase(String title);

    // Thêm phương thức phân trang
    Page<Exam> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Thêm phương thức phân trang
    Page<Exam> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    List<Exam> findByDurationBetween(Double minDuration, Double maxDuration);

    // Thêm phương thức phân trang
    Page<Exam> findByDurationBetween(Double minDuration, Double maxDuration, Pageable pageable);

    List<Exam> findByOrderByCreatedAtDesc();

    long countByExamScopeType(EnumClass.ExamScopeType scopeType);

    long countByCourse(Course course);

    long countByChapter(Chapter chapter);

    long countByUnit(Unit unit);

    // Tìm kiếm theo từ khóa ở title hoặc description
    @Query("SELECT e FROM Exam e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Exam> searchByTitleOrDescription(@Param("keyword") String keyword);

    // Tìm kiếm theo từ khóa với phân trang
    @Query("SELECT e FROM Exam e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Exam> searchByTitleOrDescription(@Param("keyword") String keyword, Pageable pageable);

    // Kiểm tra tồn tại theo tiêu đề (case insensitive)
    boolean existsByTitleIgnoreCase(String title);

    // Thêm phương thức tìm đề thi không có câu hỏi
    @Query("SELECT e FROM Exam e LEFT JOIN e.questions q WHERE q.id IS NULL")
    List<Exam> findExamsWithoutQuestions();
}
