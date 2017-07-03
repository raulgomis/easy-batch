package org.easybatch.extensions.kafka;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.writer.StandardOutputRecordWriter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;

public class ExecutorConsumer
{
    public static void main( String[] args )
    {
        executeRead();
    }

    private static void executeRead() {
        Job job;

        KafkaConsumerConnectionFactory kafkaConnectionFactory =
                new KafkaConsumerConnectionFactory(new Properties(), new HashSet<>(Collections.singletonList("test")));

        try {
            // Build a batch job
            job = new JobBuilder()
                    .named("Kafka Reader")
                    .batchSize(10)
                    .reader(new KafkaReader(kafkaConnectionFactory))
                    .writer(new StandardOutputRecordWriter())
                    .build();

            // Execute the job
            JobExecutor jobExecutor = new JobExecutor();
            JobReport report = jobExecutor.execute(job);
            jobExecutor.shutdown();

            // Print the job execution report
            System.out.println(report);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
