
lazy val appDependencies: Seq[ModuleID] = PlayCrossCompilation.dependencies(
  shared = Seq(
    "uk.gov.hmrc"       %% "domain"             % "6.2.0-play-28",
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-work-item-repo-play-28" % "0.68.0",
    "uk.gov.hmrc"       %% "http-verbs-play-28" % "13.8.0",
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-play-28"        % "0.68.0",
    "commons-codec" % "commons-codec" % "1.9",
    "com.typesafe.play" %% "play-json"          % "2.8.2",
    "com.beachape"      %% "enumeratum"         % "1.6.0",
    "com.beachape"      %% "enumeratum-play-json" % "1.6.0",
    "com.typesafe.play" %% "play-json-joda"       % "2.6.13",
    "uk.gov.hmrc"       %% "emailaddress"         % "3.5.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "uk.gov.hmrc"            %% "reactivemongo-test" % "5.0.0-play-28" % "test",
    "com.vladsch.flexmark"    % "flexmark-all"       % "0.35.10" % "test",
    "org.scalatestplus"     %% "mockito-3-4"         % "3.2.8.0" % "test"
  )
)
lazy val messageLib = Project(appName, file("."))
  .settings(majorVersion := 0)
  .settings(isPublicArtefact := true)
  .settings(
    scalaVersion := "2.12.12",
    libraryDependencies ++= appDependencies,
  ).settings(PlayCrossCompilation.playCrossCompilationSettings)

val appName = "dc-message-library"
