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
* `AutoUpdate`: a check will be performed after `update`s, and the lockfile will be automatically updated if there are
  any changes.

### dependencyLockModuleFilter

* **Description:** Excludes the specified dependencies from the lockfile.
* **Accepts:** `sbt.librarymanagement.ModuleFilter`
* **Default:** `DependencyFilter.fnToModuleFilter(_ => false)` (no exclusions)

### dependencyLockConfigurationFilter

* **Description:** Excludes the specified configurations from the lockfile.
* **Accepts:** `sbt.librarymanagement.ConfigurationFilter`
* **Default:** `DependencyFilter.fnToConfigurationFilter(_ => false)` (no exclusions)