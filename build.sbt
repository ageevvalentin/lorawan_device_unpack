name := "lorawan_device_unpack"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= {
  val scalaTestVersion = "3.0.5"

  Seq(
    "io.spray"                     %% "spray-json"                        % "1.3.4",
    "commons-codec"                % "commons-codec"                      % "1.11",
    "com.typesafe.scala-logging"   %% "scala-logging"                     % "3.8.0",
    "ch.qos.logback"               %  "logback-classic"                   % "1.2.3",
    //Testing
    "org.scalatest"                %% "scalatest"                         % scalaTestVersion  % "test",
    "org.pegdown"                  %  "pegdown"                           % "1.6.0"           % "test"
  )
}


// test options
scalacOptions in Test ++= Seq("-Yrangepos")

testOptions in Test ++= Seq(Tests.Argument("-o"), Tests.Argument("-h", "target"), Tests.Filter(s => s.endsWith("Spec")))

fork := true