package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Optional<Course> findById(String id);

    @Override
    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.units"
    })
    Page<Course> findAll(Pageable pageable);
}