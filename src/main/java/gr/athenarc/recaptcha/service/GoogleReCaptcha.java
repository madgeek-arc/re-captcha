package gr.athenarc.recaptcha.service;

import gr.athenarc.recaptcha.ReCaptchaResponse;
import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;
import gr.athenarc.recaptcha.config.ReCaptchaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleReCaptcha implements ReCaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleReCaptcha.class);
    private final ReCaptchaProperties properties;

    public GoogleReCaptcha(ReCaptchaProperties properties) {
        this.properties = properties;
    }

    @Override
    public void verify(String recaptchaFormResponse) throws InvalidReCaptchaTokenException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", properties.getSecret());
        map.add("response", recaptchaFormResponse);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,headers);

        RestTemplate restTemplate = new RestTemplate();
        ReCaptchaResponse response = restTemplate.postForObject(properties.getUrl(), request, ReCaptchaResponse.class);
        assert response != null;

        logger.info("ReCaptcha response: {}", response);

        if (response.getErrorCodes() != null) {
            logger.warn("ReCaptcha Error Codes: {}", String.join(", ", response.getErrorCodes()));
        }

        if (!response.isSuccess()){
            throw new InvalidReCaptchaTokenException("Invalid ReCaptcha");
        }
        if (properties.getVersion() != 2 && response.getScore() < properties.getThreshold()) {
            throw new InvalidReCaptchaTokenException("ReCaptcha verification failed");
        }
    }
}
