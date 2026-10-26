package gr.athenarc.recaptcha.annotation;

import gr.athenarc.recaptcha.service.ReCaptchaService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnableReCaptchaTest {

    @Configuration
    @EnableReCaptcha
    static class TestConfig {
    }

    @Test
    void registersReCaptchaService() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.setEnvironment(new MockEnvironment().withProperty("google.recaptcha.secret", "secret"));
            context.register(TestConfig.class);
            context.refresh();
            assertNotNull(context.getBean(ReCaptchaService.class));
        }
    }
}
