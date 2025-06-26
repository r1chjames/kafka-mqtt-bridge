package kafkamqttbridge;

import kafkamqttbridge.kafka.KafkaConsumer;
import kafkamqttbridge.kafka.KafkaProducer;
import kafkamqttbridge.mqtt.MqttConsumer;
import kafkamqttbridge.mqtt.MqttProducer;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import static kafkamqttbridge.AppConfigConstants.*;

@Slf4j
public class Bridge {
    public static void main(String[] args) {
        var config = new AppConfig();
        var queue = new LinkedBlockingQueue<String>();

        var input = config.get(INPUT);
        var output = config.get(OUTPUT);

    }

    private void consumeMqtt(final AppConfig config, final LinkedBlockingQueue<String> queue) {
        var consumer = new MqttConsumer(config);
        consumer
            .connect()
            .subscribe(queue::offer);
    }

    private void produceMqtt(final AppConfig config, final LinkedBlockingQueue<String> queue) {
        var producer = new KafkaProducer(config);
        try {
            producer
                .connect()
                .publish(queue.take());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consumeKafka(final AppConfig config, final LinkedBlockingQueue<String> queue) throws ConnectException {
        var consumer = new KafkaConsumer(config);
        consumer
            .connect()
            .subscribe(queue::offer);
    }

    private void produceKafka(final AppConfig config, final LinkedBlockingQueue<String> queue) throws ConnectException {
        var producer = new MqttProducer(config);
        try {
            producer
                .connect()
                .publish(queue.take());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
