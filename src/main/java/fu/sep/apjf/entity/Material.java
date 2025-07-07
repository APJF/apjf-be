package fu.sep.apjf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
    @Column(length = 36)
    private String id;

    @Column(length = 255)
    private String description;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type;                       // KANJI, GRAMMAR …

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

    /* enum */
    public enum Type {KANJI, GRAMMAR, VOCAB, LISTENING, READING, WRITING}
}