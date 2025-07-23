package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "pending_email", unique = true)
    private String pendingEmail;

    @Column(name = "address")
    private String address;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "email_verified")
    private boolean emailVerified = true;

    @Column(name = "vip_expiration")
    private LocalDateTime vipExpiration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;

    /* 1-N User → ExamResult */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private transient List<ExamResult> examResults = new ArrayList<>();

    /* 1-N User → ApprovalRequest (as creator) */
    @OneToMany(mappedBy = "creator")
    private transient List<ApprovalRequest> createdRequests = new ArrayList<>();

    /* 1-N User → ApprovalRequest (as reviewer) */
    @OneToMany(mappedBy = "reviewer")
    private transient List<ApprovalRequest> reviewedRequests = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /* 1-N User → CourseReview */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private transient List<CourseReview> courseReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningPath> learningPaths = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitProgress> unitProgresses = new HashSet<>();

}
