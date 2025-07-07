package fu.sep.cms.entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_result_answers")
public class ExamResultAnswer {

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