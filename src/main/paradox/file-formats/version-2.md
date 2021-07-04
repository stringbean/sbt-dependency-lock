# Version 2

@@@warning
This version of the lockfile is currently a proposal and has not been implemented yet.

Version 2.0.0 should add this version.
@@@

## New Features

- Handles cross-built Scala dependencies.

### Cross-Built Scala Dependencies

There are three types of dependencies that need to be handled:

1. Dependencies that are the same for all scala versions (eg Java libraries).
2. Dependencies that are only used in one scala version (eg `scala-library` or compatibility libraries).
3. Dependencies that are used in multiple scala versions but have different artifacts (eg Scala libraries).

```scala
crossScalaVersions := Seq("2.12.10", "2.13.4")

libraryDependencies ++= Seq(
  "org.apache.commons"      % "commons-lang3" % "3.9",  // scenario 1
  "org.scala-lang.modules" %% "scala-xml"     % "1.2.0" // scenario 3
)
```

Dependency JSON for scenario 1:
```json
{
  "org": "org.apache.commons",
  "name": "commons-lang3",
  "version": "3.9",
  "crossBuilt": false,
  "scalaVersions": ["2.12", "2.13"],
  "artifacts": [
    {
      "name": "commons-lang3.jar",
      "hash": "sha1:0122c7cee69b53ed4a7681c03d4ee4c0e2765da5"
    }
  ],
  "configurations": [
    "test",
    "compile"
  ]
}
```

Dependency JSON for scenario 2 (Scala auto-library):
```json
[
  {
    "org": "org.scala-lang",
    "name": "scala-library",
    "version": "2.12.10",
    "crossBuilt": false,
    "scalaVersions": [
      "2.12"
    ],
    "artifacts": [
      {
        "name": "scala-library.jar",
        "hash": "sha1:3509860bc2e5b3da001ed45aca94ffbe5694dbda"
      }
    ],
    "configurations": [
      "test",
      "compile"
    ]
  },
  {
    "org": "org.scala-lang",
    "name": "scala-library",
    "version": "2.13.4",
    "crossBuilt": false,
    "scalaVersions": [
      "2.13"
    ],
    "artifacts": [
      {
        "name": "scala-library.jar",
        "hash": "sha1:b6781c71dfe4a3d5980a514eec8a513f693ead95"
      }
    ],
    "configurations": [
      "test",
      "compile"
    ]
  }
]
```

Dependency JSON for scenario 3:
```json
{
  "org": "org.scala-lang.modules",
  "name": "scala-xml",
  "version": "1.2.0",
  "crossBuilt": true,
  "scalaVersions": ["2.12", "2.13"],
  "artifacts": [
    {
      "name": "scala-xml_2.12.jar",
      "hash": "sha1:5d38ac30beb8420dd395c0af447ba412158965e6",
      "scalaVersion": "2.12"
    },
    {
      "name": "scala-xml_2.13.jar",
      "hash": "sha1:f6abd60d28c189f05183b26c5363713d1d126b83",
      "scalaVersion": "2.13"
    }
  ],
  "configurations": [
    "test"
  ]
}
```

## Example

```json
{
  "lockVersion": 2,
  "timestamp": "2021-05-11T12:00:00.000Z",
  "configurations": [
    "compile",
    "test"
  ],
  "scalaVersions": ["2.12", "2.13"],
  "dependencies": [
    {
      "org": "org.apache.commons",
      "name": "commons-lang3",
      "version": "3.9",
      "crossBuilt": false,
      "scalaVersions": ["2.12", "2.13"],
      "artifacts": [
        {
          "name": "commons-lang3.jar",
          "hash": "sha1:0122c7cee69b53ed4a7681c03d4ee4c0e2765da5"
        }
      ],
      "configurations": [
        "test",
        "compile"
      ]
    },
    {
      "org": "org.scala-lang",
      "name": "scala-library",
      "version": "2.12.10",
      "crossBuilt": false,
      "scalaVersions": ["2.12"],
      "artifacts": [
        {
          "name": "scala-library.jar",
          "hash": "sha1:3509860bc2e5b3da001ed45aca94ffbe5694dbda"
        }
      ],
      "configurations": [
        "test",
        "compile"
      ]
    },
    {
      "org": "org.scala-lang",
      "name": "scala-library",
      "version": "2.13.4",
      "crossBuilt": false,
      "scalaVersions": ["2.13"],
      "artifacts": [
        {
          "name": "scala-library.jar",
          "hash": "sha1:b6781c71dfe4a3d5980a514eec8a513f693ead95"
        }
      ],
      "configurations": [
        "test",
        "compile"
      ]
    },
    {
      "org": "org.scala-lang.modules",
      "name": "scala-xml",
      "version": "1.2.0",
      "crossBuilt": true,
      "scalaVersions": ["2.12", "2.13"],
      "artifacts": [
        {
          "name": "scala-xml_2.12.jar",
          "hash": "sha1:5d38ac30beb8420dd395c0af447ba412158965e6",
          "scalaVersion": "2.12"
        },
        {
          "name": "scala-xml_2.13.jar",
          "hash": "sha1:f6abd60d28c189f05183b26c5363713d1d126b83",
          "scalaVersion": "2.13"
        }
      ],
      "configurations": [
        "test"
      ]
    }
  ]

}
```