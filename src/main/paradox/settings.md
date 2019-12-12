# Settings Reference

## Settings

### dependencyLockFile

* **Description:** Filename of generated lockfile.
* **Accepts:** `java.io.File`
* **Default:** `baseDirectory.value / "build.sbt.lock"`

### dependencyLockAutoCheck

* **Description:** Controls the level of lockfile checking performed after each `update` task run. 
* **Accepts:** `DependencyLockUpdateMode`
* **Default:** `DependencyLockUpdateMode.WarnOnError`

The different levels of checking available are:

* `CheckDisabled`: no checks will be performed after `update`s.
* `WarnOnError`: a check will be performed after `update`s, and a warning printed if there are any changes.
* `FailOnError`: a check will be performed after `update`s, and the build will fail if there are any changes.