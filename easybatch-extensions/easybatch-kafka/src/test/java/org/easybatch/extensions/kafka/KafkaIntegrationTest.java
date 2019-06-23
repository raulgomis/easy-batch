package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.reader.IterableRecordReader;
import org.easybatch.core.writer.CollectionRecordWriter;
import org.easybatch.core.writer.CompositeRecordWriter;
import org.easybatch.core.writer.StandardOutputRecordWriter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaIntegrationTest
{


    private static final String TOPIC = "topic1";

    @ClassRule
    public static EmbeddedKafkaRule EMBEDDED_KAFKA = new EmbeddedKafkaRule(1)
                    .kafkaPorts(9092);

    private KafkaProducerConnectionFactory kafkaProducerConnectionFactory;
    private KafkaConsumerConnectionFactory kafkaConsumerConnectionFactory;

    @BeforeClass
    public static void setUpTopics() {
        EMBEDDED_KAFKA.getEmbeddedKafka().addTopics(
                new NewTopic(TOPIC, 1, (short) 1),
                new NewTopic("topic2", 2, (short) 1),
                new NewTopic("topic3", 1, (short) 1),
                new NewTopic("topic4", 2, (short) 1)
        );
    }

    @AfterClass
    public static void destroy() {
        EMBEDDED_KAFKA.after();
    }

    @Before
    public void setUp() {

        final Properties producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, EMBEDDED_KAFKA.getEmbeddedKafka().getBrokersAsString());

        this.kafkaProducerConnectionFactory =
                new KafkaProducerConnectionFactory(producerProperties);


        final Properties consumerProperties = new Properties();
        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, EMBEDDED_KAFKA.getEmbeddedKafka().getBrokersAsString());

        this.kafkaConsumerConnectionFactory =
                new KafkaConsumerConnectionFactory(consumerProperties, Collections.singletonList(TOPIC));

    }

    private void executeWrite(final List<String> message) {

        Job job;

        // Build a batch job
        job = new JobBuilder()
                .named("Kafka Writer")
                .batchSize(10)
                .reader(new IterableRecordReader(message))
                .writer(new CompositeRecordWriter(Arrays.asList(
                        new StandardOutputRecordWriter(),
                        new KafkaRecordWriter(kafkaProducerConnectionFactory, TOPIC)
                )))
                .build();

        // Execute the job
        JobExecutor jobExecutor = new JobExecutor();
        jobExecutor.execute(job);
        jobExecutor.shutdown();
    }


    private List<String> executeRead() {

        List<String> items = new ArrayList<>();

        Job job;

        // Build a batch job
        job = new JobBuilder()
                .named("Kafka Reader")
                .batchSize(10)
                .reader(new KafkaRecordReader(kafkaConsumerConnectionFactory))
                .writer(new CollectionRecordWriter(items))
                .build();

        // Execute the job
        JobExecutor jobExecutor = new JobExecutor();
        jobExecutor.execute(job);
        jobExecutor.shutdown();

        return items;
    }


    @Test
    public void testReadAndWrite() {

        List<String> items = Arrays.asList(
                "Raul 1",
                "Raul 2",
                "Raul 3"
        );

        executeWrite(items);

        Assert.assertEquals(items, executeRead());
    }

}
