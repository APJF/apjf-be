package fu.sep.apjf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 64)
    private String tokenValue;

    @Column(name = "requested_time", nullable = false)
    private Instant requestedTime;

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TokenType type;

    public enum TokenType {
        REGISTRATION,
        RESET_PASSWORD,
        VERIFY_EMAIL
    }
}
