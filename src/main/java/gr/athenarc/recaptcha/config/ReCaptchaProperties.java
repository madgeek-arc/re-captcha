package gr.athenarc.recaptcha.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReCaptchaProperties {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaProperties.class);
    private final String header;
    private final String secret;
    private final String url;
    private final short version;
    private final double threshold;
    private final String hostname;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public ReCaptchaProperties(
            @Value("${google.recaptcha.header:g-recaptcha-response}") String header,
            @Value("${google.recaptcha.secret:}") String secret,
            @Value("${google.recaptcha.url:https://www.google.com/recaptcha/api/siteverify}") String url,
            @Value("${google.recaptcha.version:3}") short version,
            @Value("${google.recaptcha.threshold:0.5}") double threshold,
            @Value("${google.recaptcha.hostname:}") String hostname,
            @Value("${google.recaptcha.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${google.recaptcha.read-timeout-ms:5000}") int readTimeoutMs) throws Exception {
        if ("".equals(secret)) {
            throw new Exception("reCAPTCHA secret not provided. Please add property 'google.recaptcha.secret' in your configuration.");
        }
        this.header = header;
        this.secret = secret;
        this.url = url;
        this.version = version;
        this.threshold = threshold;
        this.hostname = hostname;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;

        if (getVersion() != 2 && getVersion() != 3) {
            throw new Exception("Unsupported ReCaptcha version. Valid values: 2, 3");
        }
        if (getVersion() == 2 && getThreshold() > 0.0) {
            logger.warn("Using reCAPTCHA v2 (threshold value will be ignored).");
        }
        if (getThreshold() < 0.0 || getThreshold() > 1.0) {
            throw new Exception("Invalid reCAPTCHA threshold. Valid values: 0.0 to 1.0");
        }
        if (getConnectTimeoutMs() <= 0 || getReadTimeoutMs() <= 0) {
            throw new Exception("reCAPTCHA timeouts must be positive.");
        }
    }

    public String getHeader() {
        return header;
    }

    public String getSecret() {
        return secret;
    }

    public String getUrl() {
        return url;
    }

    public short getVersion() {
        return version;
    }

    public double getThreshold() {
        return threshold;
    }

    public String getHostname() {
        return hostname;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }
}
