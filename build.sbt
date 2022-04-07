
val appName = "dc-message-library"

lazy val messageLib = Project(appName, file("."))
  .settings(majorVersion := 0)
  .settings(isPublicArtefact := true)
  .settings(
    scalaVersion := "2.12.12",
    libraryDependencies += "com.typesafe.play" %% "play" % "2.8.8",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    libraryDependencies += "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test",
  )
