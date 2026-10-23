package gr.athenarc.recaptcha.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReCaptchaPropertiesTest {

    private static ReCaptchaProperties create(String secret, short version, double threshold, int timeoutMs) throws Exception {
        return new ReCaptchaProperties("g-recaptcha-response", secret, "http://localhost/siteverify",
                version, threshold, "", timeoutMs, timeoutMs);
    }

    @Test
    void acceptsValidConfiguration() {
        assertDoesNotThrow(() -> create("secret", (short) 3, 0.5, 5000));
        assertDoesNotThrow(() -> create("secret", (short) 2, 0.0, 5000));
    }

    @Test
    void rejectsMissingSecret() {
        assertThrows(Exception.class, () -> create("", (short) 3, 0.5, 5000));
    }

    @Test
    void rejectsUnsupportedVersion() {
        assertThrows(Exception.class, () -> create("secret", (short) 0, 0.5, 5000));
        assertThrows(Exception.class, () -> create("secret", (short) 1, 0.5, 5000));
    }

    @Test
    void rejectsThresholdOutsideUnitRange() {
        assertThrows(Exception.class, () -> create("secret", (short) 3, -0.1, 5000));
        assertThrows(Exception.class, () -> create("secret", (short) 3, 1.1, 5000));
    }

    @Test
    void rejectsNonPositiveTimeouts() {
        assertThrows(Exception.class, () -> create("secret", (short) 3, 0.5, 0));
    }
}
