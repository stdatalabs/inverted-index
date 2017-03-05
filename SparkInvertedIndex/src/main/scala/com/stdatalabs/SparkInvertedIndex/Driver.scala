package com.stdatalabs.SparkInvertedIndex

/*#############################################################################################
# Description: Spark - Inverted Index
#
# Input: 
#   1. /user/cloudera/shakespeare
#		2. /user/cloudera/stopwords.txt
#
# To Run this code use the command:    
# spark-submit --class com.stdatalabs.SparkInvertedIndex.Driver \
#							 --master yarn-client \
#							 SparkInvertedIndex-0.0.1-SNAPSHOT.jar \
#							 /user/cloudera/stopwords.txt \
#							 /user/cloudera/shakespeare \
#							 /user/cloudera/SparkInvertedIndex_op
#############################################################################################*/

// Scala Imports
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.sql._
import org.apache.spark.sql.SQLContext._
import org.apache.spark.sql.hive.HiveContext

object Driver {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark - inverted Index")
    val sc = new SparkContext(conf)

    // Read file containing stopwords into a broadcast variable
    val stopWordsInput = sc.textFile(args(0))
    val stopWords = stopWordsInput.flatMap(x => x.split("\\r?\\n")).map(_.trim)
    val broadcastStopWords = sc.broadcast(stopWords.collect.toSet)

    // Delete output file if exists
    val hadoopConf = new org.apache.hadoop.conf.Configuration()
    val hdfs = org.apache.hadoop.fs.FileSystem.get(new java.net.URI("hdfs://quickstart.cloudera:8020"), hadoopConf)
    try {
      hdfs.delete(new org.apache.hadoop.fs.Path(args(2)), true)
    } catch {
      case _: Throwable => {}
    }

    // Read input file and filter all stopwords
    sc.wholeTextFiles(args(1)).flatMap {
      case (path, text) =>
        text.replaceAll("[^\\w\\s]|('s|ly|ed|ing|ness) ", " ")
          .split("""\W+""")
          .filter(!broadcastStopWords.value.contains(_)) map {
          // Create a tuple of (word, filePath)
            word => (word, path)
          }
    }.map {
      // Create a tuple with count 1 ((word, fileName), 1)
      case (w, p) => ((w, p.split("/")(6)), 1)
    }.reduceByKey {
      // Group all (word, fileName) pairs and sum the counts
      case (n1, n2) => n1 + n2
    }.map {
      // Transform tuple into (word, (fileName, count))
      case ((w, p), n) => (w, (p, n))
    }.groupBy {
      // Group by words
      case (w, (p, n)) => w
    }.map {
      // Output sequence of (fileName, count) into a comma seperated string
      case (w, seq) =>
        val seq2 = seq map {
          case (_, (p, n)) => (p, n)
        }
        (w, seq2.mkString(", "))
    }.saveAsTextFile(args(2))
  }

} 