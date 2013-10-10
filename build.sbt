import net.virtualvoid.sbt.graph.Plugin
import sbt._

name := "monotonic"

organization := "com.github.arschles"

scalaVersion := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

logBuffered := false

Plugin.graphSettings
