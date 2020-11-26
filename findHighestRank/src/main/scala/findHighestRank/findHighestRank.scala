package findHighestRank

import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.IntegerType
/**
 * This method will, for each trend in the dataset, find the location, date, and hour
 * that the trend was at it's highest trending point.
 *
 * Note: if a trend was at that point more than once (ex. trending # 1 at multiple
 * locations or times) then it will appear that many times in the output
 *
 * This trend corresponds with Find-Highest-Ranks.jar
 *
 * @param inputPath  The path to the dataset that is being queried.
 * @param outputPath The path to where the output file will be stored (preferably a csv file).
 */
object findHighestRank {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      throw new IllegalArgumentException("Exactly 2 arguments required: <trendName> <inputPath> <outputPath>")
    }

    val inputPath = args(0)
    val outputPath = args(1)

    val spark = SparkSession
      .builder()
      .appName("Find highest rank")
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

    val joinedDF = trendDS
      .select(trendDS("Trend_Name").alias("Name"), trendDS("Rank"))
      .groupBy("Name")
      .agg(functions.min("Rank").alias("Highest_trending_rank"))

    trendDS
      .join(joinedDF,
        trendDS("Rank") === joinedDF("Highest_trending_rank")
          && trendDS("Trend_Name") === joinedDF("Name"),
        "inner")
      .orderBy(asc("Highest_trending_rank"), asc("Trend_Name"))
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
