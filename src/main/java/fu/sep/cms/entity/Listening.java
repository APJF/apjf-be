package fu.sep.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reading")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Listening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url", nullable = false)
    private String description;

    @Column(name = "script", nullable = false)
    private String script;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Lesson lesson;
}
