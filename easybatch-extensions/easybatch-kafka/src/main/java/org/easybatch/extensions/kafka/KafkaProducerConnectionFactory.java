package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

class KafkaProducerConnectionFactory implements ConnectionFactory<Producer<String, String>> {

    private final Properties properties;

    KafkaProducerConnectionFactory(final Properties defaults) {

        Properties props = new Properties(defaults);
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "easy-batch-group");
        props.put("acks", "0");
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.properties = props;
    }


    @Override
    public Producer<String, String> createConnection() throws Exception {
        return new KafkaProducer<>(properties);
    }
}
