ThisBuild / scalaVersion  := "2.11.12"
ThisBuild / version       := "1.0"
ThisBuild / libraryDependencies ++= commonDependencies
//ThisBuild / organization := "com.danielhlee"

// Projects
lazy val root = project
  .in(file("."))
  .settings(
    name := "simplifiedTrending"
  )
  .aggregate(
    findAverageRank,
    findHighestRank,
    findHoursTrending,
    findNumberOfTweets,
    findNumberOfTweetsFiltered,
    showTopTrends,
    showTopTrendsFiltered,
    trendTracker
  )

// Sub-Projects
// hint: intellij alt-j provides sublime text style multiple selections highlight a selection and hit alt-j to add the next occurrence
lazy val findAverageRank = project.settings(name := "findAverageRank")
lazy val findHighestRank = project.settings(name := "findHighestRank")
lazy val findHoursTrending = project.settings(name := "findHoursTrending")
lazy val findNumberOfTweets = project.settings(name := "findNumberOfTweets")
lazy val findNumberOfTweetsFiltered = project.settings(name := "findNumberOfTweetsFiltered")
lazy val showTopTrends = project.settings(name := "showTopTrends")
lazy val showTopTrendsFiltered = project.settings(name := "showTopTrendsFiltered")
lazy val trendTracker = project.settings(name := "trendTracker")

// Settings
lazy val settings =
  commonSettings ++
  wartremoverSettings

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    // release: point-in-time releases.  considered solid, stable and perpetual in order
    Resolver.sonatypeRepo("releases"),
    // snapshots capture a work in progress and are used during development
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val compilerOptions = Seq(
  // enable add'l warnings where generated code depends on assumptions
  "-unchecked",
  // emit warning and location for usages that should be imported explicitly
  "-feature",
  // Existential types (besides wildcard types) can be written and inferred
  "-language:existentials",
  // Allow higher-kinded types
  "-language:higherKinds",
  // Allow definition of implicit functions called views
  "-language:implicitConversions",
  // Allow post fix operator notation, not recommended
  "-language:postfixOps",
  // Emit warnings and locations for usages of deprecated APIs
  "-deprecation",
  // Specify character encoding
  "-encoding",
  "utf8"
)

lazy val wartremoverSettings = Seq(
  // turn on all warts (except Throw), only for compilation
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

// Dependencies
// Two dependencies below resolves java.lang.NoClassDefFoundError on Google DataProc
// https://github.com/googleapis/java-logging/issues/276

lazy val dependencies =
  new {
        val sparkSqlV            = "2.3.2"
        val gcloudLoggingV       = "1.102.0"
        val ioGrpcV              = "1.29.0"

        val sparkSql             = "org.apache.spark"       %% "spark-sql"               % sparkSqlV
        val gcloudLogging        = "com.google.cloud"       % "google-cloud-logging"     % gcloudLoggingV
        val grpcAlts             = "io.grpc"                % "grpc-alts"                % ioGrpcV
  }

lazy val commonDependencies = Seq(
    dependencies.sparkSql,
    dependencies.gcloudLogging exclude ("io.grpc", "grpc-alts"),
    dependencies.grpcAlts
)

