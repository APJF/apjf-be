package fu.sep.apjf.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Column(length = 36)
    private String id;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnumClass.MaterialType type;                       // KANJI, GRAMMAR …

    @Column()
    private String script;

    @Column()
    private String translation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnumClass.Status status;

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