package fu.sep.cms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chapter")
@Getter
@Setter
@ToString(exclude = {"course", "units", "approvalRequests"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @Column(length = 36)
    private String id;              // UUID

    @Column(nullable = false)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    /* prerequisite chapter */
    @ManyToOne
    @JoinColumn(name = "prerequisite_chapter_id")
    private Chapter prerequisiteChapter;

    /* Course owner */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;

    /* 1-N Chapter → Unit */
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<Unit> units = new HashSet<>();

    /* 1-N Chapter → ApprovalRequest */
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<ApprovalRequest> approvalRequests = new HashSet<>();
}