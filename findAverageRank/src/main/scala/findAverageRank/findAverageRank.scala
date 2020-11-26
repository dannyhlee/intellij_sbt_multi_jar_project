package findAverageRank

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.IntegerType

/**
 * This method will, for every trend in the dataset, show the average rank of the trend and will
 * find the total number of hours it has been cumulatively trending for throughout all of the locations.
 *
 * ex: if a trend was trending for one hour in Boston and one hour in Houston, than it has been
 * trending for two hours total.
 *
 * This method corresponds with Find-Average-Rank.jar
 *
 * inputPath  The path to the dataset that is being queried.
 * outputPath The path to where the output file will be stored (preferably a csv file).
 */
object findAverageRank {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      throw new IllegalArgumentException(s"Gave ${args.length} arguments.  Exactly 2 arguments required: <inputPath> <outputPath>")
    }

    val inputPath = args(0)
    val outputPath = args(1)

    val spark = SparkSession.builder()
      .appName("Find Average Rank")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    val trendDF = spark.read.option("header", "false").csv(inputPath)
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

    val averageDS = trendDS.select("Trend_Name", "Rank")
      .groupBy("Trend_Name")
      .agg(bround(avg("Rank"), 2).alias("Average_rank_while_trending"))
      .orderBy(asc("Average_rank_while_trending"))

    val countedDS = trendDS.select(trendDS("Trend_Name").alias("Name"))
      .groupBy("Name")
      .agg(count("Name").alias("Total_hours_trending"))

    val joinedDS : Unit = averageDS.join(countedDS, averageDS("Trend_Name") === countedDS("Name"), "inner")
      .select(averageDS("Trend_Name"), averageDS("Average_rank_while_trending"), countedDS("Total_hours_trending"))
      .orderBy(asc("Average_rank_while_trending"), desc("Total_hours_trending"))
      .coalesce(1)
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .option("sep", ",")
      .save(outputPath)
  }

  //Creates the case class for the data received from the twitter queries
  case class Trend(trend_Name: String,
                   location: String,
                   date: String,
                   hour: Long,
                   rank: Long,
                   tweet_Volume: String) {}

}
