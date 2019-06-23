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

    public KafkaConsumerConnectionFactory(final Properties properties, final Collection<String> topics) {
        Properties defaults = new Properties();

        defaults.put("bootstrap.servers", "localhost:9092");
        defaults.put("group.id", "easy-batch-group");
        defaults.put("zookeeper.session.timeout.ms", 6000);
        defaults.put("enable.auto.commit", true);
        defaults.put("max.poll.records", 100);
        defaults.put("auto.commit.interval.ms", 100);
        defaults.put("session.timeout.ms", 10000);
        defaults.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        defaults.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        defaults.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Properties config = new Properties();
        config.putAll(defaults);
        config.putAll(properties);

        this.properties = config;
        this.topics = topics;
    }

    @Override
    public Consumer<String, String> createConnection() {
        final Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(topics);
        return consumer;
    }
}
