package gr.athenarc.recaptcha.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReCaptcha {

    /**
     * SpEL expression over the method parameters that yields the token. When empty, the token is read from the
     * request header configured by {@code google.recaptcha.header}.
     */
    String value() default "";

    /**
     * reCAPTCHA v3 action the token must have been issued for. Not checked when empty.
     */
    String action() default "";
}
