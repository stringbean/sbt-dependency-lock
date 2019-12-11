# sbt-dependency-lock

Generate dependency lockfiles for sbt projects similar to `package-lock.json` or `Gemfile.lock`.

## Rationale

Managing dependencies on a large project can be a difficult problem especially when requested dependencies pull in large
numbers of transitive dependencies. This can lead to scenarios where incrementing the version of a single dependency can
cause a snowball effect of dozens of updated transitive dependencies.

This plugin generates a lockfile based on the current project dependencies that can be checked into source control and
can be checked to see what dependencies have changed. 

@@@ index
* [Getting Started](getting-started.md)
* [Settings](settings.md)
* [File Formats](file-formats/index.md)
@@@