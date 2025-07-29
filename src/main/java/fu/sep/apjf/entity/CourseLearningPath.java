package fu.sep.apjf.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_learning_path")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseLearningPath {

    @EmbeddedId
    @Builder.Default
    private CourseLearningPathKey id = new CourseLearningPathKey();

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("learningPathId")
    @JoinColumn(name = "learning_path_id")
    private LearningPath learningPath;

    private int courseOrderNumber;
}


