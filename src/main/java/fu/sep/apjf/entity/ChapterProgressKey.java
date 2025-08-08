package fu.sep.apjf.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressKey implements Serializable {
    private String chapterId;
    private Long userId;
}