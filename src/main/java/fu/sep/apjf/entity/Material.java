package fu.sep.apjf.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "material")
@Getter
@Setter
@ToString(exclude = {"unit", "approvalRequests"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @Column(length = 100)
    private String id;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnumClass.MaterialType type;                       // KANJI, GRAMMAR …

    @Column(columnDefinition = "TEXT")
    private String script;

    @Column(columnDefinition = "TEXT")
    private String translation;

    /* ------- owner Unit ------- */
    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    @JsonBackReference
    private Unit unit;

    /* ------- 1-N Material → ApprovalRequest ------- */
    @OneToMany(mappedBy = "material",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<ApprovalRequest> approvalRequests = new HashSet<>();

}