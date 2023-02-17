
lazy val compile: Seq[ModuleID] = Seq(
  "uk.gov.hmrc"       %% "domain"                            % "8.1.0-play-28",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-work-item-repo-play-28" % "0.74.0",
  "uk.gov.hmrc"       %% "http-verbs-play-28"                % "14.8.0",
  "commons-codec"     %  "commons-codec"                     % "1.9",
  "com.typesafe.play" %% "play-json"                         % "2.9.4",
  "com.typesafe.play" %% "play-json-joda"                    % "2.9.4",
  "com.beachape"      %% "enumeratum"                        % "1.7.2",
  "com.beachape"      %% "enumeratum-play-json"              % "1.7.2",
)

lazy val test: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"    % Test,
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % "0.74.0"   % Test,
  "com.vladsch.flexmark"   %  "flexmark-all"            % "0.35.10"  % Test,
  "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0" % Test,
  "org.scalatestplus"      %% "scalacheck-1-17"         % "3.2.15.0" % Test,
  "org.scalacheck"         %% "scalacheck"              % "1.17.0"   % Test
)

lazy val messageLib = Project(appName, file("."))
  .settings(majorVersion := 0)
  .settings(isPublicArtefact := true)
  .settings(
    scalaVersion := "2.13.8",
    libraryDependencies ++= PlayCrossCompilation.dependencies(shared = compile ++ test),
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

val appName = "dc-message-library"
