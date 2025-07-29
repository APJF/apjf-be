package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    @NotNull Optional<Course> findById(@NotNull String id);

    @NotNull Page<Course> findAll(@NotNull Pageable pageable);

    Page<Course> findByStatus(EnumClass.Status status, Pageable pageable);

    Page<Course> findByLevel(EnumClass.Level level, Pageable pageable);

    List<Course> findByPrerequisiteCourseIsNull();

    Page<Course> findByPrerequisiteCourseIsNull(Pageable pageable);

    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Course> findByStatusAndLevel(EnumClass.Status status, EnumClass.Level level, Pageable pageable);

    // Các phương thức bổ sung
    Page<Course> findByTitleContainingIgnoreCaseAndLevelAndStatus(String title, EnumClass.Level level, EnumClass.Status status, Pageable pageable);

    Page<Course> findByTitleContainingIgnoreCaseAndLevel(String title, EnumClass.Level level, Pageable pageable);

    Page<Course> findByTitleContainingIgnoreCaseAndStatus(String title, EnumClass.Status status, Pageable pageable);

    Page<Course> findByLevelAndStatus(EnumClass.Level level, EnumClass.Status status, Pageable pageable);

    List<Course> findByLevelAndStatus(EnumClass.Level level, EnumClass.Status status);

    List<Course> findByTitleContainingIgnoreCase(String title);
}