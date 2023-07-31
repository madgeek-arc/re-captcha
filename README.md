# reCAPTCHA Spring Integration

This library handles the server-side verification of a reCAPTCHA response in a Spring-based project.
It accepts google's response and verifies it. 

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
google.recaptcha.threshold=0.5 ## 0.0 < threshold < 1.0
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
