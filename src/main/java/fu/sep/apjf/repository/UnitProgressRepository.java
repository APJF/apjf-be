package fu.sep.apjf.repository;

import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnitProgressRepository extends JpaRepository<UnitProgress, Long> {

    // Đếm tổng số Unit của 1 course
    @Query("SELECT COUNT(u) FROM Unit u WHERE u.chapter.course.id = :courseId")
    long countUnitsByCourseId(@Param("courseId") String courseId);

    // Đếm số Unit đã hoàn thành của user trong 1 course
    @Query("SELECT COUNT(up) FROM UnitProgress up " +
            "WHERE up.user = :user " +
            "AND up.unit.chapter.course.id = :courseId " +
            "AND up.completed = true")
    long countCompletedUnitsByUserAndCourse(@Param("user") User user,
                                            @Param("courseId") String courseId);
}
