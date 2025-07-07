package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_locations")
public class ExamLocation {

    @Id
    private String id;

    @Column(name = "exam_id")
    private String examId;

    @Column(name = "scope_id")
    private String scopeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type")
    private EnumClass.ExamScopeType scopeType;

    /* ==== Quan hệ ==== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;
}