// publishing
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")
addSbtPlugin("com.github.sbt" % "sbt-release"  % "1.4.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.11.0")

// code style
addSbtPlugin("de.heikoseeberger"       % "sbt-header"    % "5.10.0")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.2.4")
addSbtPlugin("org.scalameta"           % "sbt-scalafmt"  % "2.5.2")

// documentation
addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")
addSbtPlugin("com.github.sbt" % "sbt-ghpages"      % "0.8.0")
