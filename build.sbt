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

val scala2_12 = "2.12.12"
val scala2_13 = "2.13.8"

val compile: Seq[ModuleID] = Seq(
  "uk.gov.hmrc"       %% "domain"                            % "8.1.0-play-28",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-work-item-repo-play-28" % "0.74.0",
  "uk.gov.hmrc"       %% "http-verbs-play-28"                % "14.8.0",
  "commons-codec"     %  "commons-codec"                     % "1.9"
)

// Play 28 (2.8.18) specific versions - caution when upgrading
val play28: Seq[ModuleID] = Seq(
  "com.typesafe.play" %% "play-json"            % "2.8.2",
  "com.typesafe.play" %% "play-json-joda"       % "2.8.2",
  "com.beachape"      %% "enumeratum"           % "1.7.0",
  "com.beachape"      %% "enumeratum-play-json" % "1.7.0"
)

val test: Seq[ModuleID] = Seq(
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
    scalaVersion := scala2_12,
    crossScalaVersions := Seq(scala2_12, scala2_13),
    libraryDependencies ++= compile ++ play28 ++ test
  )

val appName = "dc-message-library"
