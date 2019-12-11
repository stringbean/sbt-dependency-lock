scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.commons"  %  "commons-lang3"  % "3.9",
  // lock file has 3.0.8
  "org.scalatest"       %% "scalatest"      % "3.0.7"   % Test,
)