package fu.sep.cms.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content_ingestion_job")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContentIngestionJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uploader_id", nullable = false)
    private String uploaderId;

    @Column(name = "approver_id", nullable = false)
    private String approverId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "ai_processed_draft", nullable = false)
    private String aiProcessedDraft;

    @Column(name = "staff_edited_content", nullable = false)
    private String staffEditedContent;

    @Column(name = "manager_notes")
    private String managerNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
}
