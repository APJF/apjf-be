package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseId(String courseId);

    List<Review> findByUser(User user);

    Optional<Review> findByUserAndCourse(User user, Course course);

    @Query(value = "SELECT r.course FROM Review r " +
            "GROUP BY r.course " +
            "ORDER BY AVG(r.rating) DESC " +
            "LIMIT 3")
    List<Course> findTop3RatedCourses();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Optional<Float> calculateAverageRatingByCourseId(@Param("courseId") String courseId);
}
