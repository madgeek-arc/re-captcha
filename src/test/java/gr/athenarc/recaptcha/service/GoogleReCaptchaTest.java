package gr.athenarc.recaptcha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.athenarc.recaptcha.ReCaptchaResponse;
import gr.athenarc.recaptcha.config.ReCaptchaProperties;
import gr.athenarc.recaptcha.exception.InvalidReCaptchaTokenException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GoogleReCaptchaTest {

    private static final String URL = "http://localhost/siteverify";

    private MockRestServiceServer server;

    private GoogleReCaptcha service(short version, String hostname) throws Exception {
        ReCaptchaProperties properties = new ReCaptchaProperties("g-recaptcha-response", "secret", URL,
                version, 0.5, hostname, 5000, 5000);
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        return new GoogleReCaptcha(properties, restTemplate);
    }

    private void respond(String json) {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formDataContains(Map.of("secret", "secret", "response", "token")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    void acceptsV3TokenAboveThreshold() throws Exception {
        GoogleReCaptcha service = service((short) 3, "");
        respond("{\"success\":true,\"score\":0.9,\"action\":\"submit\",\"hostname\":\"example.org\"}");
        assertDoesNotThrow(() -> service.verify("token"));
        server.verify();
    }

    @Test
    void rejectsV3TokenBelowThreshold() throws Exception {
        GoogleReCaptcha service = service((short) 3, "");
        respond("{\"success\":true,\"score\":0.1}");
        assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify("token"));
    }

    @Test
    void rejectsUnsuccessfulVerification() throws Exception {
        GoogleReCaptcha service = service((short) 2, "");
        respond("{\"success\":false,\"error-codes\":[\"invalid-input-response\"]}");
        assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify("token"));
    }

    @Test
    void ignoresScoreForV2() throws Exception {
        GoogleReCaptcha service = service((short) 2, "");
        respond("{\"success\":true}");
        assertDoesNotThrow(() -> service.verify("token"));
    }

    @Test
    void rejectsMissingTokenWithoutCallingGoogle() throws Exception {
        GoogleReCaptcha service = service((short) 3, "");
        assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify(null));
        assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify(" "));
        server.verify();
    }

    @Test
    void checksHostnameWhenConfigured() throws Exception {
        GoogleReCaptcha service = service((short) 3, "example.org");
        respond("{\"success\":true,\"score\":0.9,\"hostname\":\"other.org\"}");
        InvalidReCaptchaTokenException e =
                assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify("token"));
        assertEquals("ReCaptcha hostname mismatch", e.getMessage());
    }

    @Test
    void checksActionWhenExpected() throws Exception {
        GoogleReCaptcha service = service((short) 3, "");
        respond("{\"success\":true,\"score\":0.9,\"action\":\"login\"}");
        InvalidReCaptchaTokenException e =
                assertThrows(InvalidReCaptchaTokenException.class, () -> service.verify("token", "submit"));
        assertEquals("ReCaptcha action mismatch", e.getMessage());
    }

    @Test
    void mapsErrorCodes() throws Exception {
        ReCaptchaResponse response = new ObjectMapper().readValue(
                "{\"success\":false,\"error-codes\":[\"timeout-or-duplicate\"]}", ReCaptchaResponse.class);
        assertEquals("timeout-or-duplicate", response.getErrorCodes()[0]);
    }
}
