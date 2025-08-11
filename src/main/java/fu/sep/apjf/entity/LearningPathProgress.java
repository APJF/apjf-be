package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "learning_path_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathProgress {

    @EmbeddedId
    private LearningPathProgressKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("learningPathId")
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    private boolean completed;

    private Instant completedAt;
}
