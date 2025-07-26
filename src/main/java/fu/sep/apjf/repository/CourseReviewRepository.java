package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseReview;
import fu.sep.apjf.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourse(Course course);

    List<CourseReview> findByUser(User user);

    Optional<CourseReview> findByUserAndCourse(User user, Course course);

    @Query("SELECT r.course, AVG(r.rating) as avgRating " +
            "FROM CourseReview r GROUP BY r.course " +
            "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedCourses(Pageable pageable);

    default List<Object[]> findTopRatedCourses(int topN) {
        return findTopRatedCourses(PageRequest.of(0, topN));
    }

    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course = :course")
    Optional<Double> calculateAverageRatingByCourse(@Param("course") Course course);

}
