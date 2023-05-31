package gr.athenarc.recaptcha.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidReCaptchaTokenException extends RuntimeException {

    public InvalidReCaptchaTokenException() {
        super("ReCaptcha verification failed.");
    }

    public InvalidReCaptchaTokenException(String message) {
        super(message);
    }
}
