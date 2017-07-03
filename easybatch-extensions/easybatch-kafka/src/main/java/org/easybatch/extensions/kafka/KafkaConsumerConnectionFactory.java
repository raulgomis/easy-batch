package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collection;
import java.util.Properties;

class KafkaConsumerConnectionFactory implements ConnectionFactory<Consumer<String, String>> {

    private final Properties properties;
    private final Collection<String> topics;

    public KafkaConsumerConnectionFactory(final Properties defaults, final Collection<String> topics) {
        Properties props = new Properties(defaults);

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "easy-batch-group");
        props.put("zookeeper.session.timeout.ms", 6000);
        props.put("enable.auto.commit", true);
        props.put("max.poll.records", 10);
        props.put("auto.commit.interval.ms", 4000);
        props.put("session.timeout.ms", 10000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        this.properties = props;
        this.topics = topics;
    }

    @Override
    public Consumer<String, String> createConnection() throws Exception {
        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(topics);
        return consumer;
    }
}
