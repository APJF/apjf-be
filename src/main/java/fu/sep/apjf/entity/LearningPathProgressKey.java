package fu.sep.apjf.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathProgressKey implements Serializable {
    private Long learningPathId;
    private Long userId;
}
