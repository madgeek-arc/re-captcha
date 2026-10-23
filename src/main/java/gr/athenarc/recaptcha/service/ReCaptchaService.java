package gr.athenarc.recaptcha.service;

import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;

public interface ReCaptchaService {

    /**
     * Verifies a token. A non-empty {@code expectedAction} must match the action the v3 token was issued for.
     */
    void verify(String recaptchaFormResponse, String expectedAction) throws InvalidReCaptchaTokenException;

    default void verify(String recaptchaFormResponse) throws InvalidReCaptchaTokenException {
        verify(recaptchaFormResponse, "");
    }
}
