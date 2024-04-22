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

import sbt.Keys._

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

val hmrcMongoVersion = "1.8.0"

val compileDependencies: Seq[ModuleID] = Seq(
  "uk.gov.hmrc"       %% "domain-play-30"                    % "9.0.0",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-work-item-repo-play-30" % hmrcMongoVersion,
  "uk.gov.hmrc"       %% "http-verbs-play-30"                % "14.13.0",
  "commons-codec"      % "commons-codec"                     % "1.16.0",
  "org.playframework" %% "play-json"                         % "3.0.2",
  "com.beachape"      %% "enumeratum"                        % "1.7.3",
  "com.beachape"      %% "enumeratum-play-json"              % "1.8.0"
)

val testDependencies: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.1"          % Test,
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % Test,
  "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8"         % Test,
  "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0"       % Test,
  "org.scalatestplus"      %% "scalacheck-1-17"         % "3.2.15.0"       % Test,
  "org.scalacheck"         %% "scalacheck"              % "1.17.0"         % Test
)

lazy val messageLib = Project(appName, file("."))
  .settings(isPublicArtefact := true)
  .settings(libraryDependencies ++= compileDependencies ++ testDependencies)

Test / test := (Test / test)
  .dependsOn(scalafmtCheckAll)
  .value

val appName = "dc-message-library"
