package com.stdatalabs.MRInvertedIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;

public class InvertedIndexMapper extends Mapper<LongWritable, Text, Text, Text> {

	private static final Logger LOG = Logger
			.getLogger(InvertedIndexMapper.class);
	Set<String> stopwords = new HashSet<String>();
	Text word = new Text();

	@Override
	protected void setup(Context context) throws IOException {
		Configuration conf = context.getConfiguration();

		LOG.info("Reading cached file for HDFS");

		if (conf.getBoolean("wordcount.skip.patterns", false)) {
			URI[] localPaths = context.getCacheFiles();
			Path path = new Path(localPaths[0]);

			LOG.info("Processing cached file:" + path);

			try {
				FileSystem fs = FileSystem.get(context.getConfiguration());
				FSDataInputStream in = fs.open(path);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String pattern;
				LOG.info("Adding stopwords to hashset");
				while ((pattern = br.readLine()) != null) {
					stopwords.add(pattern);
				}
			} catch (IOException ioe) {
				System.err
						.println("Caught exception while parsing the cached file '"
								+ path);
			}

		}
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String fileName = ((FileSplit) context.getInputSplit()).getPath()
				.getName();

		String line = value
				.toString()
				.replaceAll("[^\\w\\s]|('s|ly|ed|ing|ness|.|,|\\?|'|:|;) ", " ")
				.toLowerCase();

		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			String wordText = tokenizer.nextToken();
			if (stopwords.contains(wordText))
				continue;
			word.set(wordText);
			context.write(word, new Text(fileName));
		}
	}
}
