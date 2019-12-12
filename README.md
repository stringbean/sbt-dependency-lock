# sbt-dependency-lock 
[![Build Status](https://travis-ci.com/stringbean/sbt-dependency-lock.svg?branch=master)](https://travis-ci.com/stringbean/sbt-dependency-lock)
[![Codacy grade](https://img.shields.io/codacy/grade/d45ca406c90c45c88a3a317563bc3302?label=codacy)](https://codacy.com/app/stringbean/sbt-dependency-lock)
![Bintray](https://img.shields.io/bintray/v/stringbean/sbt-plugins/sbt-dependency-lock?label=sbt%201.x) 

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
[error]     org.scala-lang.modules:scala-xml_2.12:[1.2.0]->[1.1.0] (test)
[error]     org.scalactic:scalactic_2.12:[3.0.8]->[3.0.7] (test)
[error]     org.scalatest:scalatest_2.12:[3.0.8]->[3.0.7] (test)
```

See the [docs](https://stringbean.github.io/sbt-dependency-lock) for further information on how the plugin works.