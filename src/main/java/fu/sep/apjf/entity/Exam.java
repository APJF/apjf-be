package fu.sep.apjf.entity;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    private String id;

    private String title;
    private String description;
    private Integer duration;      // minutes

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_scope_type")
    private EnumClass.ExamScopeType examScopeType;

    /* ==== Quan hệ ==== */

    /** N‑N với Question */
    @ManyToMany
    @JoinTable(
        name = "exam_questions",
        joinColumns = @JoinColumn(name = "exam_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    /** 1‑N với ExamLocation */
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamLocation> locations = new ArrayList<>();

    /** 1‑N với ExamResult */
    @OneToMany(mappedBy = "exam")
    private List<ExamResult> results = new ArrayList<>();
}