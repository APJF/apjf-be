package fu.sep.apjf.entity;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseLearningPathKey implements Serializable {
    private String courseId;
    private Long learningPathId;
}


