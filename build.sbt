val scala213 = "2.13.8"
val scala212 = "2.12.15"

val allScalaVersions          = List(scala212, scala213)
val documentationScalaVersion = scala213

ThisBuild / scalaVersion           := scala213
ThisBuild / organization           := "dev.vhonta"
ThisBuild / version                := "0.1.0-RC1"
ThisBuild / versionScheme          := Some("early-semver")
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"

val publishSettings = Seq(
  publishTo            := sonatypePublishToBundle.value,
  publishMavenStyle    := true,
  organizationHomepage := Some(url("https://vhonta.dev")),
  homepage             := Some(url("https://vhonta.dev")),
  licenses := Seq(
    "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
  ),
  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/vitaliihonta/zio-temporal-distage"),
      s"scm:git:https://github.com/vitaliihonta/zio-temporal-distage.git",
      Some(s"scm:git:git@github.com:vitaliihonta/zio-temporal-distage.git")
    )
  ),
  developers := List(
    Developer(
      id = "vitaliihonta",
      name = "Vitalii Honta",
      email = "vitalii.honta@gmail.com",
      url = new URL("https://github.com/vitaliihonta")
    )
  )
)
val coverageSettings = Seq(
  //  Keys.fork in org.jacoco.core.
  jacocoAggregateReportSettings := JacocoReportSettings(
    title = "ZIO Temporal Distage Coverage Report",
    subDirectory = None,
    thresholds = JacocoThresholds(),
    formats = Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
    fileEncoding = "utf-8"
  )
)

lazy val baseProjectSettings = Seq(
  scalacOptions ++= {
    val baseOptions = Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    )
    val crossVersionOptions = CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, y)) if y < 13 => Seq("-Ypartial-unification")
      case _                      => Seq.empty[String]
    }
    baseOptions ++ crossVersionOptions
  }
)

val crossCompileSettings: Seq[Def.Setting[_]] = {
  def crossVersionSetting(config: Configuration) =
    (config / unmanagedSourceDirectories) += {
      val sourceDir = (config / sourceDirectory).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
        case _                       => sourceDir / "scala-2.13-"
      }
    }

  Seq(
    crossVersionSetting(Compile),
    crossVersionSetting(Test)
  )
}

val noPublishSettings = Seq(
  publish / skip := true,
  publish        := {}
)

val baseSettings    = baseProjectSettings
val baseLibSettings = baseSettings ++ publishSettings ++ coverageSettings

lazy val root = project
  .in(file("."))
  .settings(baseSettings, noPublishSettings)
  .settings(
    name := "zio-temporal-distage-root"
  )
  .aggregate(
    `zio-temporal-distage-core`.projectRefs: _*
  )
  .aggregate(
    examples,
    coverage
  )

lazy val coverage = project
  .in(file("./.coverage"))
  .settings(baseSettings, coverageSettings)
  .settings(
    publish / skip := true,
    publish        := {}
  )
  .aggregate(
    `zio-temporal-distage-core`.jvm(scala213)
  )

lazy val `zio-temporal-distage-core` = projectMatrix
  .in(file("core"))
  .settings(baseLibSettings)
  .settings(crossCompileSettings)
  .settings(
    libraryDependencies ++= BuildConfig.coreLibs ++ Seq(
      BuildConfig.ScalaReflect.macros.value
    )
  )
  .jvmPlatform(scalaVersions = allScalaVersions)

lazy val examples = project
  .in(file("examples"))
  .settings(baseSettings, noPublishSettings)
  .settings(
    name         := "examples",
    scalaVersion := scala213
//    libraryDependencies ++= BuildConfig.examplesLibs
  )
  .dependsOn(
    `zio-temporal-distage-core`.jvm(scala213)
  )
