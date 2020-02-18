# Contributing

Welcome and thanks for taking the time to contribute to this project!


## Building & Preparing PRs

Before submitting a PR please ensure that you have run the following sbt tasks:

* `test`
* `scripted`
* `scalafmt`
* `headerCheck`

This should ensure that everything works and meets the styleguide.

PRs will get automatically built by [Travis CI](https://travis-ci.com/stringbean/sbt-dependency-lock) - please make sure the build passes before marking as ready.

## Documentation

The documentation site is built using [Paradox](https://github.com/lightbend/paradox) and lives in the `src/main/paradox` directory. You can preview the site by running:

```sh
sbt previewSite
```