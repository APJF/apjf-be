package fu.sep.apjf.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    private String id;

    private String content;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    /* ==== Quan hệ ==== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    /**
     * 1‑N ngược với ExamResultAnswer (khi học sinh chọn)
     */
    @OneToMany(mappedBy = "selectedOption")
    private List<ExamResultAnswer> selectedByAnswers = new ArrayList<>();
}
