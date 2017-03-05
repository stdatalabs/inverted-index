# MapReduce VS Spark - Inverted Index Example

Comparing MapReduce to Spark using Inverted Index example.

## Requirements
- IDE 
- Apache Maven 3.x
- JVM 6 or 7

## General Info
The repository contains both MapReduce and Spark projects MRInvertedIndex and SparkInvertedIndex
* com/stdatalabs/SparkInvertedIndex
     * Driver.scala --   Spark code to build inverted index
* com/stdatalabs/MRInvertedIndex
    * InvertedIndexMapper.java -- Reads files in input directory and outputs (word, filename) as key-value pair
    * InvertedIndexReducer.java -- Reads the list of (word, firstnames) key-value pair and outputs (word, (filename, count))
    * InvertedIndexDriver.java -- Driver program for MapReduce jobs

## Description
* A comparison between MapReduce and Apache Spark RDD code using Inverted Index example 
  Discussed in blog -- 
     [MapReduce VS Spark - Inverted Index Example](http://stdatalabs.blogspot.in/2017/03/mapreduce-vs-spark-inverted-index.html)

### More articles on hadoop technology stack at [stdatalabs](http://stdatalabs.blogspot.in)

