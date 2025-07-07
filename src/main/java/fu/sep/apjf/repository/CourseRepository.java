package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Override
    @EntityGraph(attributePaths = {             // chapter → unit
            "chapters",
            "chapters.units"
    })
    Optional<Course> findById(String id);
}