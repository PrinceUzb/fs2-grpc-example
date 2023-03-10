ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "fs2-grpc-example"
  )
  .aggregate(protobuf, client, server)

lazy val protobuf =
  project
    .in(file("protobuf"))
    .settings(
      scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage
    )
    .enablePlugins(Fs2Grpc)

lazy val client =
  project
    .in(file("client"))
    .dependsOn(protobuf)
    .settings(
      libraryDependencies ++= Seq(
        "io.grpc"               % "grpc-netty"           % scalapb.compiler.Version.grpcJavaVersion,
        "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      )
    )

lazy val server =
  project
    .in(file("server"))
    .dependsOn(protobuf)
    .settings(
      libraryDependencies ++= Seq(
        "io.grpc"               % "grpc-netty"           % scalapb.compiler.Version.grpcJavaVersion,
        "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      )
    )

Global / lintUnusedKeysOnLoad := false
Global / onChangedBuildSource := ReloadOnSourceChanges