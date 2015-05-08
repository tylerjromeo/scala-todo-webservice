organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  val slickV = "3.0.0"
  val slf4jV = "1.6.4"
  val postgresV = "9.4-1201-jdbc41"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.typesafe.slick"  %%  "slick"         % slickV,
    "org.slf4j"           %   "slf4j-nop"     % slf4jV,
    "org.postgresql"      %   "postgresql"    % postgresV,
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "com.h2database"      %   "h2"            % "1.4.187"
  )
}

Revolver.settings
