# Version 1 (current)

* **Added in:** 0.1.0
* **Removed in:** _N/A_
* **Schema:** [lockfile-v1.schema.json](../lockfile-v1.schema.json)

## Types

### Lockfile

Top level object for a project lockfile. Contains details of the build configurations and a list of the resolved
dependencies.

#### lockVersion

* **Type:** Integer.
* **Description:** Version of the lockfile: `1`.

#### timestamp

* **Type:** String (timestamp).
* **Description:** File generation timestamp in ISO 8601 format.

#### configurations

* **Type:** Array of strings.
* **Description:** List of sbt build configurations in the current project.

#### dependencies

* **Type:** Array of `Dependency`.
* **Description:** List of all the dependencies in the current project. 

### Dependency

Details of a resolved dependency.

#### org

* **Type:** String.
* **Description:** Organisation of the resolved dependency from Ivy/Maven.

#### name

* **Type:** String.
* **Description:** Name of the resolved dependency from Ivy/Maven. 

#### version

* **Type:** String.
* **Description:** Version of the resolved dependency.

#### artifacts

* **Type:** Array of `Artifact`.
* **Description:** List of all the artifacts for the dependency.
* **Note:** Currently only `jar` artifacts are included.

#### configurations

* **Type:** Array of strings.
* **Description:** List of the sbt configurations that include this dependency.

### Artifact

Details of an artifact contained within a dependency.

#### name

* **Type:** String.
* **Description:** Filename of the artifact.

#### hash

* **Type:** String (checksum).
* **Description:** Checksum of the artifact prefixed with the checksum algorithm.
* **Note:** Currently only `sha1` is supported.

## Example

```json
{
  "lockVersion" : 1,
  "timestamp" : "2019-10-29T17:33:05.944Z",
  "configurations" : [
    "compile",
    "optional",
    "provided",
    "runtime",
    "test"
  ],
  "dependencies" : [
    {
      "org" : "org.apache.commons",
      "name" : "commons-lang3",
      "version" : "3.9",
      "artifacts" : [
        {
          "name" : "commons-lang3.jar",
          "hash" : "sha1:0122c7cee69b53ed4a7681c03d4ee4c0e2765da5"
        }
      ],
      "configurations" : [
        "test",
        "compile",
        "runtime"
      ]
    },
    {
      "org" : "org.scala-lang",
      "name" : "scala-library",
      "version" : "2.12.10",
      "artifacts" : [
        {
          "name" : "scala-library.jar",
          "hash" : "sha1:3509860bc2e5b3da001ed45aca94ffbe5694dbda"
        }
      ],
      "configurations" : [
        "test",
        "compile",
        "runtime"
      ]
    }
  ]
}
```