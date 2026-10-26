package gr.athenarc.recaptcha.service;

import gr.athenarc.recaptcha.ReCaptchaResponse;
import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;
import gr.athenarc.recaptcha.config.ReCaptchaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class GoogleReCaptcha implements ReCaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleReCaptcha.class);
    private final ReCaptchaProperties properties;
    private final RestTemplate restTemplate;

    public GoogleReCaptcha(ReCaptchaProperties properties) {
        this(properties, createRestTemplate(properties));
    }

    GoogleReCaptcha(ReCaptchaProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    private static RestTemplate createRestTemplate(ReCaptchaProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()));
        requestFactory.setReadTimeout(Duration.ofMillis(properties.getReadTimeoutMs()));
        return new RestTemplate(requestFactory);
    }

    @Override
    public void verify(String recaptchaFormResponse, String expectedAction) throws InvalidReCaptchaTokenException {
        if (!StringUtils.hasText(recaptchaFormResponse)) {
            throw new InvalidReCaptchaTokenException("ReCaptcha token missing");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", properties.getSecret());
        map.add("response", recaptchaFormResponse);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,headers);

        ReCaptchaResponse response = restTemplate.postForObject(properties.getUrl(), request, ReCaptchaResponse.class);
        if (response == null) {
            throw new InvalidReCaptchaTokenException("Empty ReCaptcha verification response");
        }

        logger.debug("ReCaptcha response: {}", response);

        if (response.getErrorCodes() != null) {
            logger.warn("ReCaptcha Error Codes: {}", String.join(", ", response.getErrorCodes()));
        }

        if (!response.isSuccess()){
            throw new InvalidReCaptchaTokenException("Invalid ReCaptcha");
        }
        if (properties.getVersion() != 2 && response.getScore() < properties.getThreshold()) {
            throw new InvalidReCaptchaTokenException("ReCaptcha verification failed");
        }
        if (StringUtils.hasText(properties.getHostname()) && !properties.getHostname().equals(response.getHostname())) {
            throw new InvalidReCaptchaTokenException("ReCaptcha hostname mismatch");
        }
        if (properties.getVersion() != 2 && StringUtils.hasText(expectedAction) && !expectedAction.equals(response.getAction())) {
            throw new InvalidReCaptchaTokenException("ReCaptcha action mismatch");
        }
    }
}
