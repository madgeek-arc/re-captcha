# reCAPTCHA Spring Integration

This library handles the server-side verification of a reCAPTCHA response in a Spring-based project.
It accepts google's response and verifies it. 

## Role

Supplies an annotation (`@ReCaptcha`) that a Spring web application puts on controller methods to reject
requests whose reCAPTCHA token fails verification against Google's `siteverify` API.

## Prerequisites

- A Spring 6 / Spring Boot 3 web application on Java 17+ (the library uses `jakarta.servlet`), with AspectJ
  proxying and a JSON message converter (Jackson) available to `RestTemplate`.
- A reCAPTCHA v2 or v3 secret key from the Google reCAPTCHA admin console.
- To build: JDK 17+ and Maven 3.

## How to use:

1. Add the following properties to your application
```properties
# Required Property
google.recaptcha.secret=........

## The Following properties are pre-configured.
## Add them if you wish to override the default values.
google.recaptcha.header=g-recaptcha-response
google.recaptcha.url=https://www.google.com/recaptcha/api/siteverify
google.recaptcha.version=3 ## Accepted values (2,3)
google.recaptcha.threshold=0.5 ## 0.0 <= threshold <= 1.0, checked at startup
google.recaptcha.connect-timeout-ms=5000
google.recaptcha.read-timeout-ms=5000

## Optional checks, skipped when unset
google.recaptcha.hostname=my.site.org ## must match the hostname Google reports for the token
```

2. Use the annotation **@EnableReCaptcha** to import the configuration.

```java
import gr.athenarc.recaptcha.annotation.EnableReCaptcha;

@EnableReCaptcha
public class MyAppConfiguration {
    ...
}
```
3. Add the **@ReCaptcha** annotation on the controller method you wish to secure.


```java
import gr.athenarc.recaptcha.annotation.ReCaptcha;

@ReCaptcha
public ResponseEntity<?> myCoolMethod(...) {
        ...
}
```
The default functionality makes use of a specified request **header** (default: g-recaptcha-response) and uses its value to verify the user's request.
You can change the **header** using the property `google.recaptcha.header=<my-prefered-header>`. 
<br/><br/>

You can also pass the recaptcha response as a value in the annotation using a method's argument.

```java
import gr.athenarc.recaptcha.annotation.ReCaptcha;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@ReCaptcha(value = "#recaptchaResponse")
public ResponseEntity<?> myCoolMethod_1(@RequestHeader("my-cool-header") String recaptchaResponse){
        ...
}

@ReCaptcha(value = "#recaptchaResponse")
public ResponseEntity<?> myCoolMethod_2(@RequestParam String recaptchaResponse){
        ...
}
```

For reCAPTCHA v3 you can also require the token to have been issued for a specific action:

```java
@ReCaptcha(action = "submit")
public ResponseEntity<?> submit(...) {
        ...
}
```

## Build

```bash
mvn package    # runs the tests and produces target/recaptcha-<version>.jar
mvn install    # installs it into the local Maven repository
```

## Documentation

- [Architecture](docs/architecture.md)
- [Configuration](docs/configuration.md)
- [Integration](docs/integration.md)
- [Security model](docs/security-model.md) · [Security policy](SECURITY.md)
- [Development](docs/development.md)
- [Testing](docs/testing.md)
- [Contributing](CONTRIBUTING.md) · [Changelog](CHANGELOG.md)

No HTTP API, deployment or operations guides exist because the library is not deployed on its own.

## Licence

Apache License 2.0, see [LICENSE](LICENSE). Maintained by the madgeek-arc team
(<https://github.com/madgeek-arc/re-captcha>).
