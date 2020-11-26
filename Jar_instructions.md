## Instructions
To run these locally, in the shell, you will need to start hdfs and yarn.  

Upload the `sample_data_large.csv` file to hdfs (your home directory in the following example)
Make sure the output directory does not exist.

For example, to send the `findAverageRank` jar to Spark from repository base directory:  
```
spark-submit \
--class=simplifiedTrending.findAverageRank 
--conf "spark.eventlog.enabled=false"  \
target/scala-2.11/simplifiedtrending_2.11-0.1.jar sample_data_large.csv output
```
 
## Main-Class: simplifiedTrending.*

### Class name and signatures:

#### simplifiedTrending.findAverageRank(inputPath: String, outputPath: String)
This jar will, for every trend in the dataset, show the average rank of the trend and will find the total number of hours it has been cumulatively 
trending for throughout all of the locations. For example, if a trend was trending for one hour in Houston at number 5 and another hour in Boston 
at number 1, the query will return 2 total hours trending with an average rank of 3.

The format for using the jar is -jar Find-Average-Rank.jar inputPath outputPath
Where inputPath is the path to the file that contains the dataset to be queries and outputPath is the path to the folder that will hold the output.

#### simplifiedTrending.findHighestRank(inputPath: String, outputPath: String)
This jar will, for each trend in the dataset, find the location, date, and hour that that trend was at it's at its highest trending point.

Note: if a trend was at that point more than once (ex. a trend was trending at #1 at multiple locations or during multiple hours), then each instance
will appear in the output.

The format for this jar is -jar Find-Highest-Ranks.jar inputPath outputPath
Where inputPath is the path to the file that contains the dataset to be queries and outputPath is the path to the folder that will hold the output.

#### simplifiedTrending.findHoursTrending(filter: String, inputPath: String, outputPath: String)
This method will, for a given trend, find the number of hours that it was trending between every location in the dataset.

This format for this jar is  -jar Find-Hours-Trending.jar filter inputPath outputPath
Where filter is the name of the trend that you are filtering the query by, inputPath is the path to the file that contains the dataset to be queries 
and outputPath is the path to the folder that will hold the output.

#### simplifiedTrending.findNumberOfTweets(inputPath: String, outputPath: String)
One thing to note about the tweet volume(number of tweets) is that the Twitter API did not log the value for every trend, and in those cases the 
value was shown as null in the API.  We handled these values by making the value 0 rather than null, so if a trend has a tweet volume of 0, it was 
most likely not logged by the Twitter API.

This jar has two different functionalities depending on the number of parameter passed after you call the jar (either 2 or 3).

(With 2 parameters)

When ran with two parameters this jar will show every trend and the tweet volume associated with that trend.

The format for using this jar with two parameters is -jar Find-Number-Of-Tweets.jar inputPath outputPath
Where inputPath is the path to the file that contains the dataset to be queries and outputPath is the path to the folder that will hold the output.


#### simplifiedTrending.findNumberOfTweetsFiltered(filter: String, inputPath: String, outputPath: String)
One thing to note about the tweet volume(number of tweets) is that the Twitter API did not log the value for every trend, and in those cases the 
value was shown as null in the API.  We handled these values by making the value 0 rather than null, so if a trend has a tweet volume of 0, it was 
most likely not logged by the Twitter API.

This jar has two different functionalities depending on the number of parameter passed after you call the jar (either 2 or 3).

(With 3 parameters)

When ran with three parameters this jar will show a specified trends tweet volume.

The format for using this jar with three parameters is -jar Find-Number-Of-Tweets.jar filter inputPath outputPath
Where filter is the name of the trend that you are filtering the query by, inputPath is the path to the file that contains the dataset to be queries 
and outputPath is the path to the folder that will hold the output.

#### simplifiedTrending.showTopTrends(inputPath: String, outputPath: String)
--Show-Top-Trends--

This jar has two different functionalities depending on the number of parameters passed after you call the jar (either 2 or 3).

(With 2 parameters)

When ran with two parameters this jar will show every trend, as well as the location, date, and hour that it was trending if that trend was trending number
one in a location.

The format for using the jar is -jar Show-Top-Trends.jar inputPath outputPath
Where inputPath is the path to the file that contains the dataset to be queries and outputPath is the path to the folder that will hold the output.

#### simplifiedTrending.showTopTrendsFiltered(filter: String, inputPath: String, outputPath: String)
--Show-Top-Trends--

This jar has two different functionalities depending on the number of parameters passed after you call the jar (either 2 or 3).

(With 3 parameters)

When ran with three parameters this jar will, for a specified location, show every trend that trending number one in the location as well as show the date and
hour when it was trending number one.

The format for using the jar this way is -jar Show-Top-Trends.jar filter inputPath outputPath
Where filter is the location that you are filtering the query by, inputPath is the path to the file that contains the dataset to be queries and outputPath is 
the path to the folder that will hold the output.

#### simplifiedTrending.trendTracker(filter: String, inputPath: String, outputPath: String)

This method will, for a given trend, find every time that the trend was trending and will show the name, location, date, hour, and rank the trend had each time
that it was trending.

The format for this jar is =jar TrendTracker filter, inputPath, outputPath
Where filter is the name of the trend that you are filtering the query by, inputPath is the path to the file that contains the dataset to be queries 
and outputPath is the path to the folder that will hold the output.