package fu.sep.cms.entity;

import lombok.*;
import jakarta.persistence.*;
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
@Table(name = "questions")
public class Question {

    @Id
    private String id;             // UUID, sinh từ code hoặc @GenericGenerator

    private String content;

    @Column(name = "correct_answer")
    private String correctAnswer;  // với dạng tự luận; nếu chỉ MCQ thì bỏ

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
    private List<Exam> exams = new ArrayList<>();

    /**
     * 1‑N với QuestionOption
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options = new ArrayList<>();

    /**
     * 1‑N với ExamResultAnswer
     */
    @OneToMany(mappedBy = "question")
    private List<ExamResultAnswer> resultAnswers = new ArrayList<>();
}