package com.stdatalabs.MRInvertedIndex;

/*#############################################################################################
 # Description: SecondarySort using Map Reduce
 #
 # Input: 
 #   1. /user/cloudera/shakespeare
 #	2. /user/cloudera/stopwords.txt
 #
 # To Run this code use the command:    
 # yarn jar MRInvertedIndex-0.0.1-SNAPSHOT.jar \
 #		   com.stdatalabs.MRInvertedIndex.InvertedIndexDriver \
 #		   shakespeare \
 #		   MRInvertedIndex_op \
 #		   -skip stopwords.txt
 #############################################################################################*/

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class InvertedIndexDriver extends Configured implements Tool {

	private static final Logger LOG = Logger
			.getLogger(InvertedIndexDriver.class);

	public static void main(String args[]) throws Exception {
		ToolRunner.run(new InvertedIndexDriver(), args);
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length < 2) {
			System.err
					.println("Usage: wordcount -skip [wordcount stop word file] <input_file> <output_file>");
			System.exit(2);
		}

		Job job = Job.getInstance(getConf());

		for (int i = 0; i < args.length; i += 1) {
			if ("-skip".equals(args[i])) {
				job.getConfiguration().setBoolean("wordcount.skip.patterns",
						true);
				i += 1;
				job.addCacheFile(new URI(
						"hdfs://quickstart.cloudera:8020/user/cloudera/"
								+ args[i]));
				LOG.info("Added file to the distributed cache: " + args[i]);
			}
		}
		job.setJarByClass(InvertedIndexDriver.class);

		job.setMapperClass(InvertedIndexMapper.class);
		job.setReducerClass(InvertedIndexReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		Path inputFilePath = new Path(args[0]);
		Path outputFilePath = new Path(args[1]);

		FileInputFormat.addInputPath(job, inputFilePath);
		FileOutputFormat.setOutputPath(job, outputFilePath);

		FileSystem fs = FileSystem.newInstance(getConf());

		if (fs.exists(outputFilePath)) {
			fs.delete(outputFilePath, true);
		}

		job.waitForCompletion(true);
		return 0;
	}
}