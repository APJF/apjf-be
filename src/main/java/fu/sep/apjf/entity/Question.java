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
@Table(name = "question")
public class Question {

    @Id
    private String id;

    private String content;

    @Column(name = "correct_answer")
    private String correctAnswer;  // với dạng tự luận; nếu chỉ MCQ thì bỏ

    @Column(name = "scope")
    @Enumerated(EnumType.STRING)
    private EnumClass.QuestionScope scope;


    @Enumerated(EnumType.STRING)
    private EnumClass.QuestionType type;

    private String explanation;

    @Column(name = "file_url")
    private String fileUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* ==== Quan hệ ==== */

    /**
     * N‑N với Exam qua bảng trung gian exam_questions
     */
    @ManyToMany(mappedBy = "questions")
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();

    /**
     * 1‑N với QuestionOption
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();

    /**
     * 1‑N với ExamResultDetail
     */
    @OneToMany(mappedBy = "question")
    @Builder.Default
    private List<ExamResultDetail> examResultDetails = new ArrayList<>();

    /**
     * N‑N với Unit
     */
    @ManyToMany
    @JoinTable(
        name = "question_unit",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    @Builder.Default
    private List<Unit> units = new ArrayList<>();
}