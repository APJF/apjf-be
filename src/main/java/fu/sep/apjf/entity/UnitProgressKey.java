package fu.sep.apjf.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitProgressKey implements Serializable {
    private String unitId;
    private Long userId;
}


