package fu.sep.apjf.exception;

import lombok.Getter;

@Getter
public class UnverifiedAccountException extends RuntimeException {
    private final String email;

    public UnverifiedAccountException(String message, String email) {
        super(message);
        this.email = email;
    }
}
