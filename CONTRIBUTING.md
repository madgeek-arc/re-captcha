# Contributing

Contributions are welcome: bug reports, feature requests and pull requests.

## Reporting issues

Use [GitHub Issues](https://github.com/madgeek-arc/re-captcha/issues) for bugs, feature requests and
questions. For bugs, include the version, the environment, the steps to reproduce, and the expected
and actual behaviour.

## Branching

- `main` is the integration branch and always carries the next `-SNAPSHOT` version.
- Work on short-lived branches named `<type>/<short-description>` (for example `fix/recaptcha-timeouts`,
  `docs/configuration`) and open a pull request against `main`.
- Without write access to the repository, fork it, create the branch in your fork and open the
  pull request from there.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, `refactor:`,
`test:`, `build:`, `chore:`. Keep the subject in the imperative mood and under ~72 characters.

## Pull requests

- Describe the change and its motivation, and link the related issue.
- At least one maintainer review is required before merging.
- Update `README.md`/`docs/` when behaviour, configuration or the supported Spring baseline changes.
- Add an entry to `CHANGELOG.md` under *Unreleased*.

## Required checks

- `mvn -B package` must succeed.
- New behaviour should come with tests (see [docs/testing.md](docs/testing.md)).

## Releases and versioning

- [Semantic Versioning](https://semver.org/).
- Releases are cut with `maven-release-plugin` (`mvn release:prepare release:perform`), which tags
  `recaptcha-<version>` and deploys to the repository configured in `pom.xml`
  `distributionManagement`.
- Move the *Unreleased* changelog entries under the new version heading as part of the release.
