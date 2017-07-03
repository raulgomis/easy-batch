package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;
import org.easybatch.core.record.StringRecord;

import java.util.Date;
import java.util.LinkedList;

public class KafkaReader implements RecordReader {

    private final LinkedList<ConsumerRecord<String, String>> buffer;
    private Consumer<String, String> consumer;
    private final KafkaConsumerConnectionFactory factory;

    public KafkaReader(final KafkaConsumerConnectionFactory factory) {
        this.buffer = new LinkedList<>();
        this.factory = factory;
    }

    public void open() throws Exception {
        consumer = factory.createConnection();
    }

    public Record readRecord() throws Exception {
        if(buffer.isEmpty()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if(records.isEmpty()) {
                return null;
            }
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
            }
        }

        ConsumerRecord<String,String> record = buffer.poll();
        return new StringRecord(new Header(record.offset(), record.topic(), new Date()), record.value());
    }

    public void close() throws Exception {
        if(consumer != null) {
            consumer.close();
        }
        buffer.clear();
    }
}
