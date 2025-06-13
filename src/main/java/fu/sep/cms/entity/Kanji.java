package fu.sep.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kanji")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Kanji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character", nullable = false)
    private String character;

    @Column(name = "sino_vietnamese", nullable = false)
    private String reading;

    @Column(name = "stroke_count", nullable = false)
    private Integer strokeCount;

    @Column(name = "kunyomi")
    private String kunyomi;

    @Column(name = "onyomi")
    private String onyomi;

    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}
