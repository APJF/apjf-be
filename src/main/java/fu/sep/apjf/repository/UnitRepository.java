package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    List<Unit> findByChapterId(String chapterId);
    List<Unit> findByChapterIdIn(List<String> chapterIds);
}