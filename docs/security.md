# reCAPTCHA Spring Integration — Security

Describes version `2.0.0-SNAPSHOT`.

## What the library protects

`@ReCaptcha` adds bot mitigation to individual methods. It is not authentication or authorisation and
doesn't replace the host application's security configuration. (`ReCaptchaAspect`)

## Verification model

| Check | v2 | v3 | Evidence |
|---|---|---|---|
| `success == true` | ✓ | ✓ | `GoogleReCaptcha#verify` |
| `score >= google.recaptcha.threshold` | — | ✓ | `GoogleReCaptcha#verify` |
| `action` matches `@ReCaptcha(action)` | — | ✓ when `action` is set | `GoogleReCaptcha#verify` |
| `hostname` matches `google.recaptcha.hostname` | ✓ when set | ✓ when set | `GoogleReCaptcha#verify` |

Both optional checks are off by default. Set `google.recaptcha.hostname` and `@ReCaptcha(action)` so
that tokens issued for other sites or actions are rejected.

A missing token, or an empty response from Google, fails closed with HTTP 403. If Google can't be reached
within the configured timeouts, the `RestClientException` propagates (normally HTTP 500).

Failure raises `InvalidReCaptchaTokenException`, mapped to **HTTP 403**.

## Secrets

- `google.recaptcha.secret` is required and is only sent in the form body of the POST to
  `google.recaptcha.url`.
- Pointing `google.recaptcha.url` at another host would send the secret there. Treat the property as
  security-sensitive configuration.
- Use HTTPS for `google.recaptcha.url` (the default does).

## Logging and personal data

| Level | Logged | Evidence |
|---|---|---|
| `DEBUG` | The whole Google response (`success`, `score`, `action`, `challenge_ts`, `hostname`, `errorCodes`) | `GoogleReCaptcha#verify` |
| `WARN` | Google error codes, when present | `GoogleReCaptcha#verify` |

The secret and the token are never logged.

Using reCAPTCHA sends end-user signals to Google. The host application's privacy policy should
disclose this.

## SpEL evaluation

The expression in `@ReCaptcha(value)` comes from developer-written annotations and is evaluated with a
`StandardEvaluationContext` over the method arguments. User input is never parsed as an expression.
(`ReCaptchaAspect#getReCaptchaValueFromExpression`)
