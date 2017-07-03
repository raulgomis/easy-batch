package org.easybatch.extensions.kafka;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.reader.StringRecordReader;
import org.easybatch.core.writer.CompositeRecordWriter;
import org.easybatch.core.writer.FileRecordWriter;
import org.easybatch.core.writer.StandardErrorRecordWriter;

import java.util.Arrays;
import java.util.Properties;

public class ExecutorProducer
{
    public static void main( String[] args )
    {
        executeWrite();
    }

    private static void executeWrite() {
        Job job = null;
        try {

            KafkaProducerConnectionFactory kafkaProducerConnectionFactory =
                    new KafkaProducerConnectionFactory(new Properties());

            // Build a batch job
            job = new JobBuilder()
                    .named("Kafka Writer")
                    .batchSize(10)
                    .reader(new StringRecordReader("one two three"))
                    .writer(new CompositeRecordWriter(Arrays.asList(
                            new FileRecordWriter("tweets.json"),
                            new StandardErrorRecordWriter(),
                            new KafkaWriter(kafkaProducerConnectionFactory,"test")
                    )))
                    .build();

            // Execute the job
            JobExecutor jobExecutor = new JobExecutor();
            jobExecutor.execute(job);
            jobExecutor.shutdown();

            // Print the job execution report
            //System.out.println(report);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
