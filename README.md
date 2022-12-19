# sbt-dependency-lock 

[![Build Status](https://img.shields.io/github/actions/workflow/status/stringbean/sbt-dependency-lock/ci.yml?branch=main)](https://github.com/stringbean/sbt-dependency-lock/actions/workflows/ci.yml)
[![Codacy grade](https://img.shields.io/codacy/grade/d45ca406c90c45c88a3a317563bc3302?label=codacy)](https://app.codacy.com/gh/stringbean/sbt-dependency-lock)
[![Known Vulnerabilities](https://snyk.io/test/github/stringbean/sbt-dependency-lock/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/stringbean/sbt-dependency-lock?targetFile=build.sbt)
[![sbt-dependency-lock Scala version support](https://index.scala-lang.org/stringbean/sbt-dependency-lock/sbt-dependency-lock/latest.svg)](https://index.scala-lang.org/stringbean/sbt-dependency-lock/sbt-dependency-lock) 
[![GitHub Discussions](https://img.shields.io/github/discussions/stringbean/sbt-dependency-lock)](https://github.com/stringbean/sbt-dependency-lock/discussions)

An sbt plugin to create a dependency lockfile similar to `package-lock.json` for npm or `Gemfile.lock` for RubyGems.

## Quickstart

Install the plugin by adding the following to `project/plugins.sbt`:

```scala
addSbtPlugin("software.purpledragon" % "sbt-dependency-lock" % "<version>")
```

Then generate a lockfile with `sbt dependencyLockWrite`. This will resolve dependencies and output a lockfile containing
all dependencies (including transitive ones) to `build.sbt.lock`.

The lockfile can then be checked with `sbt dependencyLockCheck`:

```text
[info] Dependency lock check passed
```

A mismatch between the lockfile and current dependencies will generate an error report:

```text
[error] (dependencyLockCheck) Dependency lock check failed:
[error]   3 dependencies changed:
[error]     org.apache.commons:commons-lang3       (test)  -> (compile,test)  3.9 
[error]     org.scala-lang.modules:scala-xml_2.12  (test)                     1.2.0  -> 1.1.0 
[error]     org.scalactic:scalactic_2.12           (test)                     3.0.8  -> 3.0.7 
[error]     org.scalatest:scalatest_2.12           (test)                     3.0.8  -> 3.0.7 
```

See the [docs](https://stringbean.github.io/sbt-dependency-lock) for further information on how the plugin works.
