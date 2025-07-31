package fu.sep.apjf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(
        name = "approval_request",
        uniqueConstraints = @UniqueConstraint(           // ngăn PENDING trùng
                name = "uk_pending_target",
                columnNames = {"course_id", "chapter_id", "unit_id",
                        "material_id", "decision"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"course", "chapter", "unit", "material"})
@EqualsAndHashCode(of = "id")
public class ApprovalRequest {

    /* ---------- PK ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* ---------- target FK (chỉ 1 not-null) ---------- */
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    @JsonBackReference
    private Chapter chapter;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    @JsonBackReference
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "material_id")
    @JsonBackReference
    private Material material;

    /* ---------- enum nhận diện loại đích ---------- */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;
    /* ---------- audit ---------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;              // staff gửi yêu cầu

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewer;             // manager duyệt / từ chối

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestType requestType;       // CREATE / UPDATE / DEACTIVATE
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Decision decision = Decision.PENDING;
    @Column()
    private String feedback;

    /* ---------- validation: đúng 1 FK & khớp enum ---------- */
    @PrePersist
    @PreUpdate
    private void validateTarget() {
        long cnt = Stream.of(course, chapter, unit, material)
                .filter(Objects::nonNull).count();
        if (cnt != 1)
            throw new IllegalStateException(
                    "Exactly ONE of course/chapter/unit/material must be set");

        if ((course != null && targetType != TargetType.COURSE)
                || (chapter != null && targetType != TargetType.CHAPTER)
                || (unit != null && targetType != TargetType.UNIT)
                || (material != null && targetType != TargetType.MATERIAL))
            throw new IllegalStateException("targetType mismatches the FK");
    }

    public enum TargetType {COURSE, CHAPTER, UNIT, MATERIAL}

    public enum RequestType {CREATE, UPDATE}

    public enum Decision {PENDING, APPROVED, REJECTED}
}