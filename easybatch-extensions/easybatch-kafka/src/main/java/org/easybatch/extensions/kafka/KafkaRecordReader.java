package org.easybatch.extensions.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;
import org.easybatch.core.record.StringRecord;

import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;

public class KafkaRecordReader implements RecordReader {

    private static final long DEFAULT_POLL_TIMEOUT = 5L;

    private final LinkedList<ConsumerRecord<String, String>> buffer;
    private final ConnectionFactory<Consumer<String, String>> factory;

    private Duration pollTimeout = Duration.ofSeconds(DEFAULT_POLL_TIMEOUT);
    private Consumer<String, String> consumer;

    public KafkaRecordReader(final KafkaConsumerConnectionFactory factory) {
        this.buffer = new LinkedList<>();
        this.factory = factory;
    }

    public void open() throws Exception {
        consumer = factory.createConnection();
    }

    public Record readRecord() {
        if(buffer.isEmpty()) {
            final ConsumerRecords<String, String> records = consumer.poll(pollTimeout);
            if(records.isEmpty()) {
                return null;
            }
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
            }
        }

        final ConsumerRecord<String,String> record = buffer.poll();

        if(record == null) {
            return null;
        }

        return new StringRecord(new Header(record.offset(), record.topic(), new Date()), record.value());
    }

    public void close() {
        if(consumer != null) {
            consumer.close();
        }
        buffer.clear();
    }
}
