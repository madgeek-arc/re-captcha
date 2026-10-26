# Changelog

All notable changes to this project are documented in this file. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/).

## Unreleased

### Added
- Optional `google.recaptcha.hostname` check and `@ReCaptcha(action = "...")` check for v3 tokens.
- Connect/read timeouts for the call to Google (`google.recaptcha.connect-timeout-ms`,
  `google.recaptcha.read-timeout-ms`, default 5000 ms).
- `ReCaptchaService#verify(String, String)` (token, expected action).
- Unit tests for token verification and property validation.
- Repository documentation (`docs/`), contribution guide and this changelog.

### Changed
- **Breaking:** requires Java 17 and Spring Framework 6 (the code already used `jakarta.servlet`).
- **Breaking:** `ReCaptchaService#verify(String, String)` is the abstract method; `verify(String)` is a
  default method delegating to it, so custom implementations must implement the two-argument form.
- **Breaking:** the `ReCaptchaProperties` constructor takes the hostname and the connect/read timeouts.
- **Breaking:** `google.recaptcha.threshold` outside 0.0–1.0, or a non-positive timeout, now fails startup.
- The Google response is logged at `DEBUG` instead of `INFO`, and the token is no longer logged.
- Releases are published to `repo.madgik.di.uoa.gr`.

### Fixed
- Google's `error-codes` are now deserialised and logged.
- A missing token or an empty siteverify response is rejected with HTTP 403 instead of failing with an
  error.

### Removed
- **Breaking:** `log4j-core` (compile scope) and `log4j-to-slf4j` dependencies; consumers that relied on
  `log4j-core` transitively must declare it.

## [1.0.0](https://github.com/madgeek-arc/re-captcha/commits/recaptcha-1.0.0) (2023-09-12)

### Features

* reCAPTCHA v2/v3 token verification against Google's siteverify endpoint, triggered by the
  `@ReCaptcha` method annotation ([12c7916](https://github.com/madgeek-arc/re-captcha/commit/12c7916d10b58e32797f6c34779f1f4e38f1cf03))
* `@EnableReCaptcha` annotation that imports the library's configuration ([50e37b3](https://github.com/madgeek-arc/re-captcha/commit/50e37b3b201615b79450bb282bbf851b81feae7b))
* only `google.recaptcha.secret` is required; the header, URL, version and threshold have defaults,
  and the property prefix is `google.recaptcha` ([250e41e](https://github.com/madgeek-arc/re-captcha/commit/250e41ebbc4d37c8d8e9c3396fa2a3f080cb35c7))

### Bug Fixes

* compile on Java versions below 17 ([1ddd8b5](https://github.com/madgeek-arc/re-captcha/commit/1ddd8b5e41f9ac4a04a55bd72f4c27b4710e72f3))

### Build System

* use semantic versioning ([bb47b8a](https://github.com/madgeek-arc/re-captcha/commit/bb47b8ad5cc8778a369e9833c935dd7358357fe9))
* depend on `slf4j-api` instead of `jcl-over-slf4j` ([72dded9](https://github.com/madgeek-arc/re-captcha/commit/72dded97aa81112fa1360fdc332cb37f2d2ce6c1))
* release with the maven-release-plugin ([eb420c4](https://github.com/madgeek-arc/re-captcha/commit/eb420c428bc6d676fc11b35908c5d24ccb3ffba4))
* target Java 11 ([63aa63d](https://github.com/madgeek-arc/re-captcha/commit/63aa63d582d7132ef78ca3795e9da6988ca09f73))
* build against Spring Framework 5.3.29 ([1e229d4](https://github.com/madgeek-arc/re-captcha/commit/1e229d459e540ceaf3f203358da49204a75c6956)), ([3fe47d4](https://github.com/madgeek-arc/re-captcha/commit/3fe47d410fdfc70ed97b27edac43a72481b0380e))
* point the SCM URLs at the `re-captcha` repository ([b3384b9](https://github.com/madgeek-arc/re-captcha/commit/b3384b9ea61b6b547f0b7d7019244418029e04d6)), ([2440623](https://github.com/madgeek-arc/re-captcha/commit/2440623a7624fa744edd7374b78f76c5611651ea))

### Documentation

* add README ([dd480ca](https://github.com/madgeek-arc/re-captcha/commit/dd480ca5db0f8a180e5d2270f3d0964148298781))
* add Apache 2.0 license ([b4068cd](https://github.com/madgeek-arc/re-captcha/commit/b4068cd86792fc6e91b3f92d5d59c31668e8fe0f))
