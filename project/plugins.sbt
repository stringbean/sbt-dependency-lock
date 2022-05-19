// publishing
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.11")

// code style
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.7.0")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.1.1")

// documentation
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.1")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.9.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
