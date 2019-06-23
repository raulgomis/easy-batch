package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

class KafkaProducerConnectionFactory implements ConnectionFactory<Producer<String, String>> {

    private final Properties properties;

    KafkaProducerConnectionFactory(final Properties properties) {

        Properties defaults = new Properties();
        defaults.put("bootstrap.servers", "localhost:9092");
        defaults.put("group.id", "easy-batch-group");
        defaults.put("acks", "0");
        defaults.put("buffer.memory", 33554432);

        defaults.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        defaults.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Properties config = new Properties();
        config.putAll(defaults);
        config.putAll(properties);

        this.properties = config;
    }


    @Override
    public Producer<String, String> createConnection() {
        return new KafkaProducer<>(properties);
    }
}
