package fu.sep.apjf.repository;

import fu.sep.apjf.entity.CourseProgress;
import fu.sep.apjf.entity.CourseProgressKey;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressKey> {

    // Kiểm tra một user đã enroll 1 course chưa
    Optional<CourseProgress> findByUserAndCourseId(User user, String courseId);

    boolean existsByUserAndCourseId(User user, String courseId);

    // Lấy tất cả CourseProgress của user cho danh sách courseId (dùng cho load list tránh N+1)
    List<CourseProgress> findByUser(User user);

    @Query("SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.course.id = :courseId")
    int countTotalStudentsByCourseId(@Param("courseId") String courseId);
    @Query("SELECT cp.course.id, COUNT(cp.user.id) " +
            "FROM CourseProgress cp " +
            "WHERE cp.course.id IN :courseIds " +
            "GROUP BY cp.course.id")
    List<Object[]> countTotalStudentsForCourses(@Param("courseIds") List<String> courseIds);
}
