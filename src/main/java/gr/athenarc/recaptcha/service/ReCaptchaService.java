package gr.athenarc.recaptcha.service;

import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;

public interface ReCaptchaService {

    void verify(String recaptchaFormResponse) throws InvalidReCaptchaTokenException;
}
