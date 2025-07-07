package fu.sep.cms.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_results")
public class ExamResult {

    @Id
    private String id;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    private Float score;

    @Enumerated(EnumType.STRING)
    private EnumClass.ExamStatus status;

    @Column(name = "user_id")
    private String userId;                  // nếu có entity User thì ManyToOne

    /* ==== Quan hệ ==== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamResultAnswer> answers = new ArrayList<>();
}