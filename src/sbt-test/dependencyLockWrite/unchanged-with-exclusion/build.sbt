scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.commons"  %  "commons-lang3"  % "3.9",
  "org.scalatest"       %% "scalatest"      % "3.0.8"   % Test,
)

// One ineffective filter and one for the org.scalatest organization.
dependencyLockModuleFilter := moduleFilter(organization = "org.fake") | moduleFilter(organization = "org.scalatest")