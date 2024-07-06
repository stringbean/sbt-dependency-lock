# Version 2 (proposal)

* **Added in:** _N/A_
* **Removed in:** _N/A_
* **Schema:** [lockfile-v2.schema.json](../lockfile-v2.schema.json)

@@@warning
This version of the lockfile is currently a proposal and has not been implemented yet.

This will be added in version 2.0.0.
@@@

## Types

### Lockfile

Top level object for a project lockfile. Contains details of the build configurations and a list of the resolved
dependencies.

#### lockVersion

* **Type:** Integer.
* **Description:** Version of the lockfile: `2`.

#### timestamp

* **Type:** String (timestamp) or `null`.
* **Description:** File generation timestamp in ISO 8601 format (or `null` if timestamps are disabled).

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

#### license

* **Type:** String.
* **Description:** License of the dependency (in SPDX identifier format).

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

* **Type:** Array of `Artifact Hash`.
* **Description:** Checksum of the artifact prefixed with the checksum algorithm.


### Artifact Hash

Checksums for a dependency artifact. This is an object that contains one or more hashes for the artifact, currently only
SHA-1 and SHA-256 are supported but more may be added in the future.

#### sha1

* **Type:** String.
* **Description:** SHA-1 hash of the artifact.

#### sha256

* **Type:** String.
* **Description:** SHA-256 hash of the artifact.

## Changes from Version 1

### Timestamp is optional

Previously the `timestamp` field was always included which could cause merge conflicts for some projects. From version 2
onwards this can now be set to `null` if timestamps are disabled.

Before:

```json
{
  "version": 1,
  "timestamp": "2019-10-29T17:33:05.944Z"
}
```

After:

```json
{
  "version": 2,
  "timestamp": null
}
```

### Support for Multiple Checksum Hashes

Version 1 only supported a single hashing algorithm (SHA-1). From version 2 support for storing multiple hashes in the
lockfile has been added. This allows for more flexibility and phasing in of new hashes in a backwards compatible way.

Before:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "artifacts": [
    {
      "name": "commons-lang3.jar",
      "hash": "sha1:0122c7cee69b53ed4a7681c03d4ee4c0e2765da5"
    }
  ]
}
```

After:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "artifacts": [
    {
      "name": "commons-lang3.jar",
      "hash": {
        "sha1": "0122c7cee69b53ed4a7681c03d4ee4c0e2765da5",
        "sha256": "de2e1dcdcf3ef917a8ce858661a06726a9a944f28e33ad7f9e08bea44dc3c230"
      }
    }
  ]
}
```

### License Metadata for Dependencies

The license of each dependency has been added to the `Dependency` type. This makes it easier for other tooling to
inspect the licenses used by a project.

Before:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "artifacts": [...]
}
```

After:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "license": "Apache-2.0",
  "artifacts": [...]
}
```

### Source URL Metadata for Artifacts

The source URL of each artifact has been added to the `Artifact` type. This allows other tooling to download the
artifacts resolved by sbt.

Before:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "artifacts": [
    {
      "name": "commons-lang3.jar"
    }
  ]
}
```

After:

```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "artifacts": [
    {
      "name": "commons-lang3.jar",
      "url": "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar"
    }
  ]
}
```

## Examples

### With Timestamp

```json
{
  "lockVersion": 2,
  "timestamp": "2024-06-04T17:33:05.944Z",
  "configurations": [
    "compile",
    "optional",
    "provided",
    "runtime",
    "test"
  ],
  "dependencies": [
    {
      "org": "org.apache.commons",
      "name": "commons-lang3",
      "version": "3.9",
      "license": "Apache-2.0", 
      "artifacts": [
        {
          "name": "commons-lang3.jar",
          "url": "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar",
          "hash": {
            "sha1": "0122c7cee69b53ed4a7681c03d4ee4c0e2765da5",
            "sha256": "de2e1dcdcf3ef917a8ce858661a06726a9a944f28e33ad7f9e08bea44dc3c230"
          }
        }
      ],
      "configurations": [
        "test",
        "compile",
        "runtime"
      ]
    },
    {
      "org": "org.scala-lang",
      "name": "scala-library",
      "version": "2.12.10",
      "license": "Apache-2.0",
      "artifacts": [
        {
          "name": "scala-library.jar",
          "url": "https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.10/scala-library-2.12.10.jar",
          "hash": {
            "sha1": "3509860bc2e5b3da001ed45aca94ffbe5694dbda",
            "sha256": "0a57044d10895f8d3dd66ad4286891f607169d948845ac51e17b4c1cf0ab569d"
          }
        }
      ],
      "configurations": [
        "test",
        "compile",
        "runtime"
      ]
    }
  ]
}
```

### Without Timestamp

```json
{
  "lockVersion": 2,
  "timestamp": null,
  "configurations": [
    "compile",
    "optional",
    "provided",
    "runtime",
    "test"
  ],
  "dependencies": [
    {
      "org": "org.scala-lang",
      "name": "scala-library",
      "version": "2.12.10",
      "license": "Apache-2.0",
      "artifacts": [
        {
          "name": "scala-library.jar",
          "url": "https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.10/scala-library-2.12.10.jar",
          "hash": {
            "sha1": "3509860bc2e5b3da001ed45aca94ffbe5694dbda",
            "sha256": "0a57044d10895f8d3dd66ad4286891f607169d948845ac51e17b4c1cf0ab569d"
          }
        }
      ],
      "configurations": [
        "test",
        "compile",
        "runtime"
      ]
    }
  ]
}
```
