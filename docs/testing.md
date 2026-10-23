# reCAPTCHA Spring Integration — Testing

## Current state

Unit tests run with JUnit 5 through `maven-surefire-plugin`, as part of `mvn test` and `mvn package`.
There's no CI configuration. (`pom.xml`, `src/test`)

| Test class | Covers |
|---|---|
| `config/ReCaptchaPropertiesTest` | Startup validation: missing secret, unsupported version, threshold outside 0.0–1.0, non-positive timeouts |
| `service/GoogleReCaptchaTest` | `GoogleReCaptcha#verify` against a `MockRestServiceServer`: v2/v3 success and failure, score threshold, missing token, hostname and action checks, `error-codes` mapping |

```bash
mvn test
```

## Not yet covered

| Area | Approach |
|---|---|
| `ReCaptchaAspect` header and SpEL extraction | Spring context test with a dummy `@ReCaptcha` controller and a mocked `ReCaptchaService` |
