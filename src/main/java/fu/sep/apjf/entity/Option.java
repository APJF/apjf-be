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
@Table(name = "option")
public class Option {

    @Id
    private String id;

    private String content;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "selectedOption")
    @Builder.Default
    private List<ExamResultDetail> selectedByAnswers = new ArrayList<>();
}