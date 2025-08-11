package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "unit_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitProgress {

    @EmbeddedId
    @Builder.Default
    private UnitProgressKey id = new UnitProgressKey();

    @ManyToOne
    @MapsId("unitId")
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean completed;

    private Instant completedAt;
}