package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.easybatch.core.record.Batch;
import org.easybatch.core.record.Record;
import org.easybatch.core.writer.RecordWriter;

public class KafkaRecordWriter implements RecordWriter {

    private Producer<String, String> producer;
    private final String topic;
    private final ConnectionFactory<Producer<String, String>> factory;

    public KafkaRecordWriter(final KafkaProducerConnectionFactory factory, final String topic) {
        this.factory = factory;
        this.topic = topic;
    }

    public void open() throws Exception {
        producer = factory.createConnection();
    }

    public void writeRecords(Batch batch) {
        for (Record record : batch) {
            String message = (String) record.getPayload();
            producer.send(new ProducerRecord<>(topic, message));
        }
    }

    public void close() {
        if(producer != null) {
            producer.close();
        }
    }
}
