import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

object BuildSettings {
  val Organization = "info.pudgestats"
  val ScalaVersion = "2.11.1"

  val BuildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked"),
    resolvers += "Apache Public" at "https://repository.apache.org/content/groups/public/",
    resolvers += Classpaths.typesafeReleases,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  val SharedDependencies = Seq(
    "com.google.guava" % "guava" % "17.0",
    "org.slf4j" % "slf4j-log4j12" % "1.7.7",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
  )

  val ConfigDependencies = Seq(
    "com.typesafe" % "config" % "1.2.1"
  )

  val CliAppDependencies = Seq(
    "commons-cli" % "commons-cli" % "1.3-SNAPSHOT"
  ) ++ ConfigDependencies
}

object ProjectBuild extends Build {
  val RabbitMQVersion     = "3.3.5"
  val GsonVersion         = "2.3"
  val ScalatraVersion     = "2.3.0"
  val ProjectVersion      = "1.0.1"
  val WebsiteVersion      = "1.0.2"
  val CoreVersion         = "1.0.0"

  lazy val root = Project(
    "root",
    file("."),
    settings = BuildSettings.BuildSettings ++
               Seq(
                 name := "Pudge Stats",
                 version := ProjectVersion
               )
  )

  lazy val libsteam = Project(
    "libsteam",
    file("libsteam"),
    settings = BuildSettings.BuildSettings ++ 
               Seq(
                 name := "libsteam",
                 version := ProjectVersion,
                 exportJars := true,
                 libraryDependencies ++= Seq(
                   "bouncycastle" % "bcprov-jdk15" % "140",
                   "com.google.protobuf" % "protobuf-java" % "2.5.0",
                   "com.google.code.gson" % "gson" % GsonVersion,
                   "org.apache.httpcomponents" % "httpclient" % "4.3.3",
                   "org.apache.httpcomponents" % "httpcore" % "4.3.2"
                 ) ++ BuildSettings.SharedDependencies
               )
  )

  lazy val replayFetcher = Project(
    "replay-fetcher",
    file("replay-fetcher"),
    settings = BuildSettings.BuildSettings ++
               packageArchetype.java_application ++
               Seq(
                 name in Rpm := "pudgestats-replay-fetcher",
                 version in Rpm := ProjectVersion,
                 rpmVendor in Rpm := "pudgestats",
                 rpmGroup in Rpm := Some("Applications/File"),
                 rpmLicense in Rpm := Some("MIT"),
                 packageDescription in Rpm := "The pudgestats replay fetcher.",
                 packageSummary in Rpm := "Pudgestats Replay Fetcher Package",
                 rpmBrpJavaRepackJars in Rpm := true,
                 name := "replay-fetcher",
                 version := ProjectVersion,
                 libraryDependencies ++= Seq(
                   "org.apache.commons" % "commons-compress" % "1.8.1" 
                 ) ++ BuildSettings.SharedDependencies ++ BuildSettings.CliAppDependencies
               )
  ) dependsOn(libsteam, pudgeStatsCore)

  lazy val replayParser = Project(
    "replay-parser",
    file("replay-parser"),
    settings = BuildSettings.BuildSettings ++
               packageArchetype.java_application ++
               Seq(
                 name in Rpm := "pudgestats-replay-parser",
                 version in Rpm := ProjectVersion,
                 rpmVendor in Rpm := "pudgestats",
                 rpmGroup in Rpm := Some("Applications/File"),
                 rpmLicense in Rpm := Some("MIT"),
                 packageDescription in Rpm := "The pudgestats replay parser.",
                 packageSummary in Rpm := "Pudgestats Replay Parser Package",
                 rpmBrpJavaRepackJars in Rpm := true,
                 name := "replay-parser",
                 version := ProjectVersion,
                 libraryDependencies ++= Seq(
                   "com.skadistats" % "clarity" % "1.1-SNAPSHOT" 
                     excludeAll(
                       ExclusionRule(organization = "ch.qos.logback"),
                       ExclusionRule(organization = "com.google.guava"))
                 ) ++ BuildSettings.SharedDependencies ++ BuildSettings.CliAppDependencies
               )
  ) dependsOn(pudgeStatsCore)

  lazy val pudgeStatsCore = Project(
    "pudgestats-core",
    file("pudgestats-core"),
    settings = BuildSettings.BuildSettings ++
               Seq(
                 name := "pudgestats-core",
                 version := CoreVersion,
                 exportJars := true,
                 libraryDependencies ++= Seq(
                   "joda-time" % "joda-time" % "2.4",
                   "com.google.code.gson" % "gson" % GsonVersion,
                   "com.rabbitmq" % "amqp-client" % RabbitMQVersion
                 ) ++ BuildSettings.SharedDependencies ++ BuildSettings.ConfigDependencies
               )
  )
  
  lazy val pudgeStatsWeb = Project(
    "pudgestats-web",
    file("pudgestats-web"),
    settings = Defaults.defaultSettings ++ 
               BuildSettings.BuildSettings ++
               scalateSettings ++ 
               ScalatraPlugin.scalatraWithJRebel ++
               Seq(
                 name := "pudgestats-web",
                 version := WebsiteVersion,
                 libraryDependencies ++= Seq(
                   "c3p0" % "c3p0" % "0.9.1.2",
                   "postgresql" % "postgresql" % "8.4-701.jdbc4",
                   "org.openid4java" % "openid4java" % "0.9.8",
                   "org.squeryl" %% "squeryl" % "0.9.5-7",
                   "org.scalatra" %% "scalatra" % ScalatraVersion,
                   "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
                   "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
                   "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
                   "org.eclipse.jetty.orbit" % "javax.servlet" % 
                     "3.0.0.v201112011016" % "container;provided;test" 
                     artifacts (Artifact("javax.servlet", "jar", "jar"))
                 ) ++ BuildSettings.SharedDependencies,
                 scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
                   Seq(
                     TemplateConfig(
                       base / "webapp" / "WEB-INF" / "templates",
                       Seq.empty, // default imports should be added here
                       Seq(
                         Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", 
                           importMembers = true, isImplicit = true)
                       ), // add extra bindings here
                       Some("templates")
                     )
                   )
                 }
               )
  ) dependsOn(pudgeStatsCore, libsteam)
}
