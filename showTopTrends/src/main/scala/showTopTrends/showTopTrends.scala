package main.scala.showTopTrends

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.asc
import org.apache.spark.sql.types.IntegerType

/**
 * This method will, for every trend in the dataset, show at what point in time and in which
 *  location that it was at it's highest rending point.
 *
 * This function corresponds with Show-Top-Trends when using two parameters.
 *
 * Note: if a trend is at its highest for more than one point (ex. trending # 1 for multiple hours
 *  or cities) then it will appear more than once in the output.
 *
 * @param inputPath:   The path to the dataset that is being queried.
 * @param outputPath:  The path to where the output file will be stored (preferably a csv file).
 */
object showTopTrends {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      throw new IllegalArgumentException("Exactly 2 arguments required: <trendName> <inputPath> <outputPath>")
    }

    val inputPath  = args(0)
    val outputPath = args(1)

    val spark = SparkSession
      .builder()
      .appName("Show Top Trends")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    val trendDF = spark.read
      .option("header", "false")
      .csv(inputPath)
      .withColumnRenamed("_c0", "Trend_Name")
      .withColumnRenamed("_c1", "Location")
      .withColumnRenamed("_c2", "Date")
      .withColumnRenamed("_c3", "Hour")
      .withColumnRenamed("_c4", "Rank")
      .withColumnRenamed("_c5", "Tweet_Volume")
      .withColumn("Hour", $"Hour".cast(IntegerType))
      .withColumn("Rank", $"Rank".cast(IntegerType))
      .withColumn("Tweet_Volume", $"Tweet_Volume".cast(IntegerType))

    val trendDS = trendDF.as[Trend]

    trendDS
      .filter(trend => trend.rank == 1)
      .select("Trend_Name", "Location", "Date", "Hour", "Rank")
      .orderBy(asc("Location"), asc("Date"), asc("Hour"))
      .coalesce(1)
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .option("sep", ",")
      .save(outputPath)
  }

  //Creates the case class for the data received from the twitter queries
  case class Trend(trend_Name: String, location: String, date: String, hour: Long, rank: Long, tweet_Volume: String) {}
}
