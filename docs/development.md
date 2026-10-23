# reCAPTCHA Spring Integration — Development

## Tools

| Tool | Version | Evidence |
|---|---|---|
| JDK | 17 or later (`release` 17) | `pom.xml` `maven-compiler-plugin` |
| Maven | 3.x (no wrapper in the repository) | repository root |

There's no Maven wrapper, linter, formatter or code generation configured.

## Build and install

```bash
mvn test        # unit tests
mvn package     # target/recaptcha-<version>.jar
mvn install     # makes the SNAPSHOT available to local consumers
```

Verified: `mvn package` succeeds with all tests passing (JDK 25, Maven 3).

## Trying changes in a host application

1. `mvn install` in this repository.
2. In the host application, point the `gr.athenarc.recaptcha:recaptcha` dependency at the installed
   `-SNAPSHOT` version.
3. Configure `google.recaptcha.secret` (see [configuration.md](configuration.md)). Google's published
   test keys can be used to get deterministic responses during development.
4. Enable `DEBUG` logging for `gr.athenarc.recaptcha` to see the Google response.

## Debugging notes

- If `@ReCaptcha` seems to have no effect, check that AspectJ auto-proxying is active in the host and
  that the annotated method is `public` and called through the Spring proxy. Self-invocation bypasses
  the proxy-based aspect (pointcut `execution(public * *(..))`).
- A `ClassCastException`/`NoClassDefFoundError` around `jakarta.servlet.http.HttpServletRequest` means
  the host is on `javax.servlet` (Spring 5), which the library doesn't support.
