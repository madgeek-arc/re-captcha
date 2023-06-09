package gr.athenarc.recaptcha.annotation;


import gr.athenarc.recaptcha.config.ReCaptchaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ ReCaptchaConfig.class })
@Configuration
public @interface EnableReCaptcha {
}
