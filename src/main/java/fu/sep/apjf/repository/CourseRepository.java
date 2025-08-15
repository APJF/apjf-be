package fu.sep.apjf.repository;

import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    Optional<Course> findById(String id);

    Page<Course> findAll(Pageable pageable);

    @Query("""
        SELECT new fu.sep.apjf.dto.response.CourseResponseDto(
            c.id,
            c.title,
            c.description,
            c.duration,
            c.level,
            c.image,
            c.requirement,
            c.status,
            c.prerequisiteCourse.id,
            CAST(COALESCE(AVG(r.rating), 0) AS float)
        )
        FROM Course c
        LEFT JOIN Review r ON r.course.id = c.id
        GROUP BY c.id, c.title, c.description, c.duration, c.level, c.image, c.requirement, c.status, c.prerequisiteCourse.id
    """)
    List<CourseResponseDto> findAllWithAverageRating();



}