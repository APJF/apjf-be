package fu.sep.cms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "topic", nullable = false, unique = true)
    private String topic;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "estimated_duration", nullable = false)
    private String estimatedDuration;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @OneToMany(mappedBy="subject", cascade = CascadeType.ALL)
    private Set<Chapter> chapters = new HashSet<>();

}
