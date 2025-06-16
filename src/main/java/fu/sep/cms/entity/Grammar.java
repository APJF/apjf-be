package fu.sep.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grammar")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Grammar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url", nullable = false)
    private String structure;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Lesson lesson;
}
