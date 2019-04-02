organization := "Altuera"
name := "court_address_resolver"
version := "0.0.3"

scalaVersion := "2.12.8"

mainClass in Compile := Some("com.altuera.courts.CourtAddressResolverServlet")

scalacOptions += "-Ypartial-unification" // 2.11.9+
scalacOptions += "-feature"

libraryDependencies ++= Seq(

	"javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
	"ch.qos.logback" % "logback-classic" % "1.2.3",
	"com.typesafe" % "config" % "1.3.3",
	"io.spray" %% "spray-json" % "1.3.5",
	"com.softwaremill.sttp" %% "core" % "1.5.11"
)

//если хотите версию томката которая не совпадает с версией по умолчанию
//containerLibs in Tomcat := Seq("com.github.jsimone" % "webapp-runner" % "7.0.34.1" intransitive())

enablePlugins(JettyPlugin, TomcatPlugin)

fork in run := true
