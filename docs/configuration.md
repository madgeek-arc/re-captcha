# reCAPTCHA Spring Integration — Configuration

The host application supplies all settings as Spring properties, through any Spring property source
(`application.properties`/`.yml`, environment variables via relaxed binding, command-line arguments).
They're read with `@Value` in `src/main/java/gr/athenarc/recaptcha/config/ReCaptchaProperties.java`.

| Property | Required | Default | Accepted values | Description |
|---|---|---|---|---|
| `google.recaptcha.secret` | **Yes** | empty | reCAPTCHA secret key | Shared secret sent to Google. If it's empty, context startup fails with `reCAPTCHA secret not provided...` |
| `google.recaptcha.header` | No | `g-recaptcha-response` | HTTP header name | Header read when `@ReCaptcha` has no `value` |
| `google.recaptcha.url` | No | `https://www.google.com/recaptcha/api/siteverify` | URL | Verification endpoint |
| `google.recaptcha.version` | No | `3` | `2`, `3` | Any other value fails startup. With `2`, the score threshold is ignored |
| `google.recaptcha.threshold` | No | `0.5` | `0.0`–`1.0` | Minimum v3 score. Values outside the range fail startup |
| `google.recaptcha.hostname` | No | empty | host name | When set, the hostname Google reports for the token must match it. Not checked when empty |
| `google.recaptcha.connect-timeout-ms` | No | `5000` | positive `int` | Connect timeout for the call to Google |
| `google.recaptcha.read-timeout-ms` | No | `5000` | positive `int` | Read timeout for the call to Google |

The expected v3 action is set per method with `@ReCaptcha(action = "...")`, not as a property.

## Example (placeholders only)

```properties
google.recaptcha.secret=<recaptcha-secret-key>
google.recaptcha.version=3
google.recaptcha.threshold=0.5
# google.recaptcha.hostname=<served-domain>
# google.recaptcha.header=g-recaptcha-response
# google.recaptcha.url=https://www.google.com/recaptcha/api/siteverify
```

`google.recaptcha.secret` can come from the environment variable `GOOGLE_RECAPTCHA_SECRET`: Spring's
system-environment property source maps the dotted name to it, which `@Value` placeholders use too.

## Environment differences

The library has no profiles. Per environment, use the matching Google reCAPTCHA site/secret pair for
the domain being served. Google's documented test keys can be used for local development.

## Secrets management

- Never commit `google.recaptcha.secret`. Inject it at deploy time (environment variable or secret
  store).
- The library never logs the secret or the token. It logs the Google response at `DEBUG`. See
  [security-model.md](security-model.md).

## Notes

- With `version=2`, the startup log always prints `Using reCAPTCHA v2 (threshold value will be ignored).`
  because the threshold defaults to `0.5`. (`ReCaptchaProperties` constructor)
- SpEL expressions in `@ReCaptcha(value)` resolve parameter names at runtime. The host must compile
  with `-parameters` (Spring Boot's parent POM enables this). Spring Framework 6.1+ no longer reads
  names from debug symbols, so without the flag the expression can't refer to parameters.
