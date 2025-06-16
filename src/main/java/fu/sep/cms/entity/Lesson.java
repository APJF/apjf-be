package fu.sep.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Kanji kanji;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Vocabulary vocabulary;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Grammar grammar;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Reading reading;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Listening listening;
}
