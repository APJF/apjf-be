package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_result_detail")
public class ExamResultDetail {

    @Id
    private String id;

    @Column(name = "user_answer")
    private String userAnswer;        // tự luận; MCQ thì = selectedOption.content

    @Column(name = "is_correct")
    private Boolean isCorrect;

    /* ==== Quan hệ ==== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id")
    private ExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    /**
     * Option mà học sinh chọn (nếu dạng MCQ)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;
}