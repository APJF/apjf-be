package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Token;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findTopByUserAndTypeOrderByRequestedTimeDesc(User user, Token.TokenType type);

    Optional<Token> findTopByUserOrderByRequestedTimeDesc(User user);

    void deleteAllByUserAndType(User user, Token.TokenType type);
}
