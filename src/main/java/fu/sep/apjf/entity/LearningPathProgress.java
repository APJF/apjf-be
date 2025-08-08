package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "learningPathProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseProgress> courseProgresses = new HashSet<>();

    private boolean completed;

    private LocalDateTime completedAt;
}
