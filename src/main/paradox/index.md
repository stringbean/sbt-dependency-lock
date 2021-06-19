# sbt-dependency-lock

Generate dependency lockfiles for sbt projects similar to `package-lock.json` or `Gemfile.lock`.

## Rationale

Managing dependencies on a large project can be a difficult problem especially when requested dependencies pull in large
numbers of transitive dependencies. This can lead to scenarios where incrementing the version of a single dependency can
cause a snowball effect of dozens of updated transitive dependencies.

This plugin generates a lockfile based on the current project dependencies that can be checked into source control and
can be checked to see what dependencies have changed. 

## Alternatives

### sbt-lock

[sbt-lock](https://github.com/tkawachi/sbt-lock) is an sbt plugin that generates lockfiles to control the resolved
dependency versions. When enabled it will generate a `lock.sbt` file that sets `Compile / dependencyOverrides` to the
currently resolved versions, any further changes to the dependencies will be overridden until the lockfile is
regenerated.

While `sbt-lock` is good at fixing the versions that sbt will use for future builds it is weak at showing what
dependencies have changed. Until the lockfile is 'unlocked' any dependency changes you make to `build.sbt` will be
ignored; this forces you to 'unlock', update the dependencies, 'lock' again and then diff the lockfile to see what has
changed.

The approach taken by `sbt-dependency-lock` is to allow changes to be made to the dependencies, warn you that the
dependencies have changed and generate a report showing the changes. Keeping the lockfile up to date can be enforced
using a lockfile check in a CI pipeline.

@@@ index
* [Getting Started](getting-started.md)
* [Settings](settings.md)
* [File Formats](file-formats/index.md)
@@@