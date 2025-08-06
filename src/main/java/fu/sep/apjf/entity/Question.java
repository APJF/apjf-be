package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(mappedBy = "questions")
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    @Builder.Default
    private List<ExamResultDetail> examResultDetails = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "question_unit",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    private Set<Unit> units = new HashSet<>();

}