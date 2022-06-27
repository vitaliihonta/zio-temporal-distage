import sbt.Keys.scalaVersion

import sbt._

object BuildConfig extends Dependencies {
  val baseLibs = Seq(
    ScalaExt.kindProjectorCompilerPlugin,
    Zio.self,
    ZioTemporal.core,
    Testing.scalatest,
  )

  val coreLibs = baseLibs ++ Seq(
    ZioTemporal.macroUtils,
    Distage.core,
    Distage.config
  )
}

trait Dependencies {

  object version {
    val zio        = "1.0.15"
    val izumi      = "1.0.8"
    val zioTemporal  = "0.1.0-RC2"
  }

  object org {
    val izumi    = "io.7mind.izumi"
    val zio      = "dev.zio"
    val vhonta   = "dev.vhonta"
  }

  object ZioTemporal {
    val core = org.vhonta %% "zio-temporal-core" % version.zioTemporal
    val macroUtils = org.vhonta %% "zio-temporal-macro-utils" % version.zioTemporal
  }

  object Zio {
    val self        = org.zio %% "zio"              % version.zio
    val interopCats = org.zio %% "zio-interop-cats" % "2.5.1.1"
  }

  object Distage {
    val core    = org.izumi %% "distage-core"              % version.izumi
    val config  = org.izumi %% "distage-extension-config"  % version.izumi
    val testKit = org.izumi %% "distage-testkit-scalatest" % version.izumi % Test
  }

  object Utility {
    val scalaJava8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"
    val izumiReflect     = org.zio                  %% "izumi-reflect"      % "2.1.0"
  }

  object ScalaReflect {

    val macros = Def.setting {
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    }

    val runtime = Def.setting {
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    }
  }

  object ScalaExt {

    val kindProjectorCompilerPlugin = compilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
    )
  }

  object Logging {
    val logstage             = org.izumi %% "logstage-core"          % version.izumi
    val logstageSlf4jAdapter = org.izumi %% "logstage-adapter-slf4j" % version.izumi

    val test = List(logstage, logstageSlf4jAdapter).map(_ % Test)
  }

  object Testing {
    val scalatest = "org.scalatest" %% "scalatest" % "3.2.12" % Test
  }

  object Examples {
    val logstage             = org.izumi %% "logstage-core"          % version.izumi
    val logstageSlf4jAdapter = org.izumi %% "logstage-adapter-slf4j" % version.izumi
  }
}
