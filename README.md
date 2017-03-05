# MapReduce VS Spark - Secondary Sort Example

Comparing MapReduce to Spark using Secondary Sort example.

## Requirements
- IDE 
- Apache Maven 3.x
- JVM 6 or 7

## General Info
The repository contains both MapReduce and Spark projects MRSecondarySort and SparkSecondarySort
* com/stdatalabs/SparkSecondarySort
     * Driver.scala --   Spark code to perform Secondary Sorting
* com/stdatalabs/MRSecondarySort
    * PersonMapper.java -- Reads lastname and firstname and outputs (Person, firstname) as key-value pair
    * PersonReducer.java -- Reads the list of (Person, firstname) key-value pair and outputs sorted list of (lastname, firstname) in 2 output files
    * PersonPartitioner.java -- Partitions the Person composite key based on lastname
    * PersonSortingComparator.java -- Sorts the mapper output based on lastname and then firstname
    * PersonGroupingComparator.java -- Groups keys with its list of values before sending to reducer
    * Driver -- Driver program for MapReduce jobs

## Description
* A comparison between MapReduce and Apache Spark RDD code using Secondary Sort example 
  Discussed in blog -- 
     [MapReduce VS Spark - Secondary Sort Example](http://stdatalabs.blogspot.in/2017/02/mapreduce-vs-spark-secondary-sort.html)

### More articles on hadoop technology stack at [stdatalabs](http://stdatalabs.blogspot.in)

