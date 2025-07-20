package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_result")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /* ==== Quan hệ ==== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExamResultDetail> details = new ArrayList<>();
}