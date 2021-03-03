name := "MappyParser"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  // to handle ingestion of dataset and query it
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",

  // test framework
  "org.scalactic" %% "scalactic" % "3.2.2",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test"

)