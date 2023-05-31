package gr.athenarc.recaptcha.aspect;

import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;
import gr.athenarc.recaptcha.config.ReCaptchaProperties;
import gr.athenarc.recaptcha.annotation.ReCaptcha;
import gr.athenarc.recaptcha.service.ReCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;

@Aspect
@Component
public class ReCaptchaAspect {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaAspect.class);
    private final ReCaptchaService service;
    private final ReCaptchaProperties properties;

    public ReCaptchaAspect(ReCaptchaService service, ReCaptchaProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @Around("execution(public * *(..)) && @annotation(gr.athenarc.recaptcha.annotation.ReCaptcha)")
    public Object verifyCaptchaResponse(final ProceedingJoinPoint point) throws Throwable {
        logger.debug("Verifying reCaptcha");

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        ReCaptcha reCaptchaAnnotation = method.getAnnotation(ReCaptcha.class);

        final String captchaResponse;
        if (reCaptchaAnnotation.value().equals("")) {
            captchaResponse = getRecaptchaHeaderFromServlet();
        } else {
            captchaResponse = getReCaptchaValueFromExpression(point, reCaptchaAnnotation.value());
        }

        logger.debug("ReCaptcha Header: {}={}", properties.getHeader(), captchaResponse);
        service.verify(captchaResponse);

        return point.proceed();
    }

    private String getReCaptchaValueFromExpression(ProceedingJoinPoint joinPoint, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);

        StandardEvaluationContext context = new StandardEvaluationContext();
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        if (exp.getValue(context) instanceof String recaptchaResponse)
            return recaptchaResponse;
        throw new InvalidReCaptchaTokenException("ReCaptcha provided is not a String");
    }

    private String getRecaptchaHeaderFromServlet() throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request;
        if (requestAttributes instanceof NativeWebRequest) {
            request = (HttpServletRequest) ((NativeWebRequest) requestAttributes).getNativeRequest();
        } else {
            throw new Exception("Request not found.");
        }

        return request.getHeader(properties.getHeader());
    }
}
