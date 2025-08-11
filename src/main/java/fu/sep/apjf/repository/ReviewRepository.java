package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT r.course, AVG(r.rating) as avgRating " +
            "FROM Review r GROUP BY r.course " +
            "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedCourses(Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Optional<Float> calculateAverageRatingByCourseId(@Param("courseId") String courseId);
}
