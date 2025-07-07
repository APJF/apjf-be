package fu.sep.apjf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unit")
@Getter
@Setter
@ToString(exclude = {"chapter", "materials", "approvalRequests"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @Column(length = 36)
    private String id;          // UUID

    @Column(nullable = false)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnumClass.Status status;

    /* prerequisite unit */
    @ManyToOne
    @JoinColumn(name = "prerequisite_unit_id")
    private Unit prerequisiteUnit;

    /* owner Chapter */
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    private Chapter chapter;

    /* 1-N Unit → Material */
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<Material> materials = new HashSet<>();

    /* 1-N Unit → ApprovalRequest */
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<ApprovalRequest> approvalRequests = new HashSet<>();
}