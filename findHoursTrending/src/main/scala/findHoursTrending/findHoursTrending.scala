package findHoursTrending

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.countDistinct
import org.apache.spark.sql.types.IntegerType


/**
 * This method will, for a given trend, find the number of hours that it was trending
 * between every location in the dataset.
 *
 * This method corresponds with Find-Hours-Trending.jar
 *
 * Ex. if the trend was trending at the same time in two different cities, it would count
 * as one hour.
 *
 * @param filter     :    the name of the trend that will be shown in the query.
 * @param inputPath  :   the path to the dataset that is being queried.
 * @param outputPath :  the path to the file where the output will be stored (preferably a csv file).
 */
object findHoursTrending {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      throw new IllegalArgumentException("Exactly 3 arguments required: <trendName> <inputPath> <outputPath>")
    }

    val filter = args(0)
    val inputPath = args(1)
    val outputPath = args(2)

    val spark = SparkSession
      .builder()
      .appName("Find Hours Trending")
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

    val newDF = trendDS.filter(trend => trend.trend_Name.equalsIgnoreCase(filter))

    newDF
      .select("Trend_Name", "Hour")
      .groupBy("Trend_Name")
      .agg(countDistinct("Hour").alias("Hours_trending"))
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
