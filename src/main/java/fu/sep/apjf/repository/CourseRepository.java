package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    Optional<Course> findById(String id);

    Page<Course> findAll(Pageable pageable);

    @Query("""
        SELECT DISTINCT c
        FROM Course c
        LEFT JOIN FETCH c.topics t
    """)
    List<Course> findAllCoursesWithTopics();

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.topics WHERE c.id IN :courseIds")
    List<Course> findCoursesWithTopicsByIds(@Param("courseIds") List<String> courseIds);

    @Query("""
        SELECT DISTINCT c FROM Course c
        LEFT JOIN FETCH c.chapters ch
        LEFT JOIN FETCH ch.units u
        LEFT JOIN FETCH u.materials m
        LEFT JOIN FETCH c.topics t
        WHERE c.id = :id
    """)
    Optional<Course> findCourseWithStructureById(@Param("id") String id);


}