/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt.Keys.*

val appName = "dc-message-library"

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "3.3.6"

val hmrcMongoVersion = "2.11.0"

val compileDependencies: Seq[ModuleID] = Seq(
  "uk.gov.hmrc"       %% "domain-play-30"                    % "13.0.0",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-work-item-repo-play-30" % hmrcMongoVersion,
  "uk.gov.hmrc"       %% "http-verbs-play-30"                % "15.7.0",
  "commons-codec"      % "commons-codec"                     % "1.20.0",
  "org.playframework" %% "play-json"                         % "3.0.6",
  "org.jsoup"          % "jsoup"                             % "1.18.3"
)

val testDependencies: Seq[ModuleID] = Seq(
  "uk.gov.hmrc"            %% "domain-test-play-30"     % "13.0.0"         % Test,
  "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.2"          % Test,
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % Test,
  "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8"         % Test,
  "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0"       % Test,
  "org.scalatestplus"      %% "scalacheck-1-17"         % "3.2.18.0"       % Test,
  "org.scalacheck"         %% "scalacheck"              % "1.19.0"         % Test
)

lazy val messageLib = Project(appName, file("."))
  .settings(isPublicArtefact := true)
  .settings(
    libraryDependencies ++= compileDependencies ++ testDependencies,
    scalacOptions ++= Seq("-language:implicitConversions")
  )
  .settings(ScoverageSettings())

Test / test := (Test / test)
  .dependsOn(scalafmtCheckAll)
  .value
