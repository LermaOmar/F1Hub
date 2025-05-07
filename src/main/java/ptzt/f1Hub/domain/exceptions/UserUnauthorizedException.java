package ptzt.f1Hub.domain.exceptions;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
