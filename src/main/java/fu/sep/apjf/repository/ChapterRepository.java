package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findByCourse(Course course);

    List<Chapter> findByTitleContainingIgnoreCase(String title);

    List<Chapter> findByCourseAndStatus(Course course, EnumClass.Status status);

    boolean existsByCourseAndTitleIgnoreCase(Course course, String title);
}