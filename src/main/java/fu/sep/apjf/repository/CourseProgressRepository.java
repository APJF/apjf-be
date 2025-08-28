package fu.sep.apjf.repository;

import fu.sep.apjf.dto.response.CourseProgressPercentResponseDto;
import fu.sep.apjf.dto.response.CourseTotalEnrollResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseProgress;
import fu.sep.apjf.entity.CourseProgressKey;
import fu.sep.apjf.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressKey> {

    // Kiểm tra một user đã enroll 1 course chưa
    Optional<CourseProgress> findByUserAndCourseId(User user, String courseId);

    boolean existsByUserAndCourseId(User user, String courseId);

    boolean existsByCourseAndUserIdAndCompleted(Course course, Long userId, boolean completed);

    // Lấy tất cả CourseProgress của user cho danh sách courseId (dùng cho load list tránh N+1)
    List<CourseProgress> findByUserId(Long userId);

    @Query("SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.course.id = :courseId")
    int countTotalStudentsByCourseId(@Param("courseId") String courseId);
    @Query("SELECT cp.course.id, COUNT(cp.user.id) " +
            "FROM CourseProgress cp " +
            "WHERE cp.course.id IN :courseIds " +
            "GROUP BY cp.course.id")
    List<Object[]> countTotalStudentsForCourses(@Param("courseIds") List<String> courseIds);

    @Query("""
        SELECT new fu.sep.apjf.dto.response.CourseProgressPercentResponseDto(
            c.id,
            c.title,
            c.level,
            CAST(SUM(CASE WHEN cp.completed = true THEN 1 ELSE 0 END) AS int),
            CAST(COUNT(cp) AS int),
            CAST((SUM(CASE WHEN cp.completed = true THEN 1 ELSE 0 END) * 100.0 / COUNT(cp)) AS float)
        )
        FROM Course c
        LEFT JOIN CourseProgress cp ON cp.course = c
        GROUP BY c.id, c.title, c.level
    """)
    List<CourseProgressPercentResponseDto> getCourseProgressPercent();
    @Query(value = """
        SELECT 
            DATE_TRUNC('month', cp.create_at) AS month,
            COUNT(*) AS total_enrolled,
            SUM(CASE WHEN cp.completed = true THEN 1 ELSE 0 END) AS total_completed
        FROM course_progress cp
        WHERE cp.create_at >= :startDate
        GROUP BY DATE_TRUNC('month', cp.create_at)
        ORDER BY month DESC
        LIMIT 6
    """, nativeQuery = true)
    List<Object[]> findLast6MonthsStats(@Param("startDate") Instant startDate);
}
