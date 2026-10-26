# reCAPTCHA Spring Integration — Integrations

Describes version `2.0.0-SNAPSHOT`. This page documents the
*observed* interfaces, not presumed platform wiring.

| Direction | Counterpart | Protocol | Purpose | Configuration key | Evidence path/symbol |
|---|---|---|---|---|---|
| consumed | Google reCAPTCHA `siteverify` (external) | HTTPS POST, form-urlencoded request, JSON response | Verify a client reCAPTCHA token | `google.recaptcha.url`, `google.recaptcha.secret` | `src/main/java/gr/athenarc/recaptcha/service/GoogleReCaptcha.java#verify` |
| provided | Host Spring application (in-process) | Java annotations and a Spring bean | Guard annotated methods | `google.recaptcha.*` | `annotation/EnableReCaptcha.java`, `annotation/ReCaptcha.java`, `aspect/ReCaptchaAspect.java` |

Which applications embed it is recorded in each consumer's own documentation.

## Provided interfaces

There's no HTTP API or OpenAPI document. The contract is Java-level:

| Element | Contract |
|---|---|
| `@EnableReCaptcha` | Put it on a configuration class. It imports `ReCaptchaConfig`, which component-scans `gr.athenarc.recaptcha` |
| `@ReCaptcha` | On a public method. When `value` is empty, the token comes from the request header named by `google.recaptcha.header`. Otherwise `value` is a SpEL expression over the method parameters, for example `#recaptchaResponse`. A non-empty `action` must match the action of a v3 token |
| `ReCaptchaService#verify(String token, String expectedAction)` | Returns normally when the token is accepted. Otherwise throws `InvalidReCaptchaTokenException`. `verify(String)` is the same with no expected action |
| `InvalidReCaptchaTokenException` | `@ResponseStatus(HttpStatus.FORBIDDEN)` |

## Outbound request to Google

`GoogleReCaptcha#verify` sends `POST {google.recaptcha.url}` with
`Content-Type: application/x-www-form-urlencoded` and the fields `secret` and `response`, and
deserialises the reply into `ReCaptchaResponse` (`success`, `score`, `action`, `challenge_ts`,
`hostname`, `error-codes`).

The call is rejected when:

1. the token is missing or blank (Google isn't called),
2. `success` is `false`,
3. `google.recaptcha.version` isn't `2` and `score < google.recaptcha.threshold`,
4. `google.recaptcha.hostname` is set and differs from `hostname`, or
5. the version isn't `2`, `@ReCaptcha(action)` is set and differs from `action`.

## Failure behaviour

| Situation | Behaviour | Evidence |
|---|---|---|
| Token invalid, expired, below threshold, or hostname/action mismatch | `InvalidReCaptchaTokenException` → HTTP 403 | `GoogleReCaptcha#verify` |
| `@ReCaptcha(value)` expression doesn't evaluate to a `String` | `InvalidReCaptchaTokenException("ReCaptcha provided is not a String")` | `ReCaptchaAspect#getReCaptchaValueFromExpression` |
| Header-based lookup outside a web request | `java.lang.Exception("Request not found.")`, which isn't mapped to a 4xx | `ReCaptchaAspect#getRecaptchaHeaderFromServlet` |
| Header missing or blank | `InvalidReCaptchaTokenException` → HTTP 403, without calling Google | `GoogleReCaptcha#verify` |
| Google unreachable or HTTP error | `RestClientException` propagates to the caller. No retry or fallback | `GoogleReCaptcha#verify` |
| Google slow | Connect/read timeouts (`google.recaptcha.*-timeout-ms`, default 5 s) end the call with a `RestClientException` | `GoogleReCaptcha#createRestTemplate` |
| Empty response body | `InvalidReCaptchaTokenException` → HTTP 403 | `GoogleReCaptcha#verify` |
