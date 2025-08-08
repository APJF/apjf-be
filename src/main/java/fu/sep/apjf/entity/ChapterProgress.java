package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterProgress {

    @EmbeddedId
    @Builder.Default
    private ChapterProgressKey id = new ChapterProgressKey();

    @ManyToOne
    @MapsId("chapterId")
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean completed;

    private LocalDateTime completedAt;
}
