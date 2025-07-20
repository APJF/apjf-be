package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam")
public class Exam {

    @Id
    private String id;

    private String title;
    private String description;
    private Double duration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_scope_type")
    private EnumClass.ExamScopeType examScopeType;

    /* Course relationship */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /* Chapter relationship */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    /* Unit relationship */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    /* ==== Quan hệ ==== */

    /**
     * N‑N với Question
     */
    @ManyToMany
    @JoinTable(
            name = "exam_question",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

 
    /**
     * 1‑N với ExamResult
     */
    @OneToMany(mappedBy = "exam")
    private List<ExamResult> results = new ArrayList<>();
}