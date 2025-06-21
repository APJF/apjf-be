package fu.sep.cms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "slot")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference   // tránh vòng lặp JSON
    private Chapter chapter;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // giữ vai trò "cha" trong JSON, giúp tránh vòng lặp
    private List<Material> materials = new ArrayList<>();

}
