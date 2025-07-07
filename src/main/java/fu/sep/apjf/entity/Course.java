package fu.sep.apjf.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString(exclude = {"chapters", "topics", "approvalRequests"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @Column(length = 36)
    private String id;                       // UUID

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(name = "duration", nullable = false)
    private BigDecimal duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Level level;

    private String image;

    @Column(length = 255)
    private String requirement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnumClass.Status status;

    /* prerequisite course */
    @ManyToOne
    @JoinColumn(name = "prerequisite_course_id")
    private Course prerequisiteCourse;

    /* 1-N Course → Chapter */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<Chapter> chapters = new HashSet<>();

    /* N-N Course ↔ Topic */
    @ManyToMany
    @JoinTable(name = "course_topic",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    @JsonManagedReference
    @Builder.Default
    private Set<Topic> topics = new HashSet<>();

    /* 1-N Course → ApprovalRequest */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<ApprovalRequest> approvalRequests = new HashSet<>();

    public enum Level {BEGINNER, INTERMEDIATE, ADVANCED}
}