# Getting Started

## Setup

Install the plugin by adding the following to `project/plugins.sbt`:

@@@vars
```scala
addSbtPlugin("software.purpledragon" % "sbt-dependency-lock" % "$project.version$")
```
@@@

And then generate a lockfile with `sbt dependencyLockWrite`. This will resolve dependencies and output a lockfile
containing all dependencies (including transitive ones) to `build.sbt.lock`.

@@@ note
The generated `build.sbt.lock` file should be checked into source control with the rest of the project source code.
@@@

## Checking for Dependency Changes

The status of the lockfile can be checked using the `dependencyLockCheck` which will resolve the current dependencies
and check them against the lockfile.

@@@ note { title=Hint }
Adding `dependencyLockCheck` to your CI build is a great way to catch dependency changes.
@@@

### Valid Lockfile

If the lockfile and current dependencies match then a success message will be printed, and the build will succeed:

```text
[info] Dependency lock check passed
```

### Missing Lockfile

If no lockfile can be found then an error will be printed, and the build will fail:

```text
[error] (dependencyLockCheck) no lockfile
```

### Lockfile Mismatch

A mismatch between the lockfile and current dependencies will generate an error report summarising the differences:

```text
[error] (dependencyLockCheck) Dependency lock check failed:
[error]   3 dependencies changed:
[error]     org.apache.commons:commons-lang3       (test)  -> (compile,test)  3.9 
[error]     org.scala-lang.modules:scala-xml_2.12  (test)                     1.2.0  -> 1.1.0 
[error]     org.scalactic:scalactic_2.12           (test)                     3.0.8  -> 3.0.7 
[error]     org.scalatest:scalatest_2.12           (test)                     3.0.8  -> 3.0.7 
```

The error report is broken down into a number of sections:

1. Configurations added:
    ```text
    1 config added: it
    ```
2. Configurations removed:
    ```text
    2 configs removed: it,war
    ```
3. Dependencies added:
    ```text
    2 dependencies added:
      com.example:artifact1  (compile)  1.0
      com.example:artifact2  (test)     1.2
    ```
4. Dependencies removed:
    ```text
    1 dependency removed:
      com.example:artifact3  (runtime)  3.1.1
    ```
5. Changed dependencies:
    ```text
    3 dependencies changed:
      org.example:both     (compile)       -> (compile,test)  1.0  -> 2.0
      org.example:configs  (compile,test)  -> (compile)       1.0
      org.example:version  (compile)                          1.0  -> 2.0
    ```