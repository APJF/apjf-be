package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "learning_path")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String targetLevel;
    private String primaryGoal;
    private String focusSkill;

    @Enumerated(EnumType.STRING)
    private EnumClass.PathStatus status; // PASSED, STUDYING

    private BigDecimal duration;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CourseLearningPath> courseLearningPaths = new ArrayList<>();
}

