package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgress {

    @EmbeddedId
    @Builder.Default
    private CourseProgressKey id = new CourseProgressKey();

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private boolean completed;

    private LocalDateTime completedAt;
}