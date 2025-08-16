package fu.sep.apjf.repository;

import fu.sep.apjf.entity.ChapterProgress;
import fu.sep.apjf.entity.ChapterProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, ChapterProgressKey> {

    Optional<ChapterProgress> findByUserAndChapterId(User user, String chapterId);

    List<ChapterProgress> findByUser(User user);

    List<ChapterProgress> findByChapterId(String chapterId);

    // ✅ check tồn tại
    boolean existsByUserAndChapter(User user, Chapter chapter);

    // hoặc nếu bạn chỉ muốn check theo chapterId
    boolean existsByUserAndChapterId(User user, String chapterId);
}
