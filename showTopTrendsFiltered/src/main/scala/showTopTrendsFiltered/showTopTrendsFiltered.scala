package showTopTrendsFiltered

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.asc
import org.apache.spark.sql.types.IntegerType

/**
 * This function will show the name, date, hour, and rank for every trend that was trending at the number one spot
 * for a given location.
 *
 * This function corresponds with Show-Top-Trends when using three parameters.
 *
 * @param filter     :    The location that the query will filter by
 * @param inputPath  :  The path to the dataset that is being queried.
 * @param outputPath The path to the file where the output will be stored (preferably a csv file.)
 */
object showTopTrendsFiltered {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      throw new IllegalArgumentException("Exactly 3 arguments required: <trendName> <inputPath> <outputPath>")
    }

    val filter = args(0)
    val inputPath = args(1)
    val outputPath = args(2)

    val spark = SparkSession
      .builder()
      .appName("Show Top Trends with Location")
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
      .filter(trend => trend.rank == 1 && trend.location.equalsIgnoreCase(filter))
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
