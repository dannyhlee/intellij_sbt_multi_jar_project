# intellij_sbt_multi_jar_project

### credits:
This project would not have been possible without this [@pbassiner](https://github.com/pbassiner)'s repo, [sbt-multi-project-example](https://github.com/pbassiner/sbt-multi-project-example).

### aim: multiple jars in a single folder/project

My goal was to create multiple thin jars in a single folder.  It was necessary to break each function out into its own module/folder.  This example has 8 methods written by [@edwardreed1](https://github.com/EdwardReed1) for our [project 2](https://github.com/EdwardReed1/revature_project_2).  

They are:
- findAverageRank
- findHighestRank
- findHoursTrending
- findNumberOfTweets
- findNumberOfTweetsFiltered
- showTopTrends
- showTopTrendsFiltered

Instructions for using the methods and the method signatures are available in `Jar_instructions.md`.

To generate your own jars, open in Intellij, open sbt shell, `clean`, `compile`, `package`.  Delete the old jars if you want to make sure they're really generating.

Each of these functions are in their own module folder, with a full stack of directories to the `fcn.scala` file.  (e.g. `fcn/src/main/scala/fcn/fcn.scala`).  Part of the trouble I had was that I put the files directly in `fcn/src/fcn.scala` and after the jar was created Google DataProc was giving me a "main class" not found exception.

The other part of the puzzle was the `build.sbt` file and using the `.aggregate` method to make the `./src/main/scala/simplified/Trending/root.scala` dependent on all the other methods.  I added other functionality I learned from Pol Bassiner's example.  This includes adding the `wartremover` plugin in `./project/plugins.sbt`.  I also added some Google cloud dependencies because I was testing these jars on Google Cloud DataProc.

If you have any questions about it, feel free to reach out to me or leave a comment/issue.

### Running these in Google Cloud DataProc

I have become quite a fan of DataProc, its very easy to use and their $300 credit is plenty to try stuff out.  To get started there:

- goto [https://cloud.google.com/dataproc](https://cloud.google.com/dataproc) and signup, active your free credit account and follow the [docs](https://cloud.google.com/dataproc/docs) and setup your google cloud storage, any security/keys, turn on services.
- **important** setup billing alerts! setup billing alerts! setup billing alerts! In case you leave something running, you don't want to overpay. 
- Open 4 windows
  1. [Dataproc cluster console](https://console.cloud.google.com/dataproc/clusters)
  2. [Dataproc jobs console](https://console.cloud.google.com/dataproc/jobs)
  3. [Cloud storage console](https://console.cloud.google.com/storage/browser/)
  4. [Billing](https://console.cloud.google.com/billing/)
- Bookmark them in a folder so you can open them all up at once
- Click `create cluster` in window 1.  There are many options, but if you're like me looking at all the options can be too much.  Click `create`.  It takes about 5-10 minutes to startup.  If you're coming to AWS, you know that's a b-u-t-full thing.
- Make a directory like `d8trends`.  Something memorable because you will be typing this often as `gs://d8trends/filefolder` as your arguments and jar when submitting a job to the cluster.  
- Upload your jar to `d8trends`.  Make a subdirectory named `input`.  If you cloned this repo, upload the sample dataset (`trends_full_dataset.csv.gz`) in the `sample-data` directory to `input`.  
- Click `submit a job` in window 2.  Change `job type` to Spark.  Paste the URI of the jar file in the `Main class or jar` line.  You can also just type it in `simplifiedTrending.trendTracker` and put the jar uri on your google cloud storage on the next line `Jar files` as `gs://d8trends/trendTracker.jar`.  Scroll down to arguments and put them in, in order.  For trend tracker you need 3 arguments.  For the sample data you can use `Gucci`, hit enter, `gs://d8trends/input`, hit enter, and `gs://d8/output` and hit enter.  Dataproc creates the output dir for you and also will overwrite results, rather than create an `output1` dir.  Click `submit`.
- When the job finishes, goto window 3 and check the output directory, you can download the csv from there.  That is, when you finally get it to work.  If your experience is like mine, you will have a screenful of failed jobs submitted.
- Tips: Its handy to click on an old job or a failed job and `clone` it so its already filled out.  Especially when troubleshooting.  Check your results.  I leave the window 4 (billing) open, because its important to remember to check your usage and *very important* go back to window 1 and shutdown your cluster.



