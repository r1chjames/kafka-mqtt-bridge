package kafkamqttbridge;

import kafkamqttbridge.kafka.KafkaConsumer;
import kafkamqttbridge.kafka.KafkaProducer;
import kafkamqttbridge.mqtt.MqttConsumer;
import kafkamqttbridge.mqtt.MqttProducer;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static kafkamqttbridge.AppConfigConstants.*;
import static kafkamqttbridge.InputOutput.*;

@Slf4j
public class Bridge {

    public static final int TERMINATION_TIMEOUT = 5;

    public static void main(final String[] args) {
        new Bridge().start();
    }

    /**
     * Starts the Kafka-MQTT bridge based on the provided configuration.
     * This method sets up the message queue, determines the bridge direction,
     * and launches consumer and producer threads.
     *
     */
    public void start() {
        var config = new AppConfig();
        var queue = new LinkedBlockingQueue<String>();

        var input = config.getString(INPUT);
        var output = config.getString(OUTPUT);

        var executor = Executors.newFixedThreadPool(2);

        try {
            if (MQTT.getValue().equalsIgnoreCase(input) && KAFKA.getValue().equalsIgnoreCase(output)) {
                log.info("Starting MQTT to Kafka bridge...");
                executor.submit(() -> {
                    try {
                        consumeMqtt(config, queue);
                    } catch (Exception e) {
                        log.error("Error in MQTT consumer thread", e);
                    }
                });

                var kafkaProducer = new KafkaProducer(config);
                kafkaProducer.connect();
                executor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            produceKafkaMessage(kafkaProducer, queue);
                        }
                    } catch (Exception e) {
                        log.error("Error in Kafka producer thread", e);
                    } finally {
                        log.info("Closing Kafka producer...");
                        kafkaProducer.close();
                    }
                });
            } else if (KAFKA.getValue().equalsIgnoreCase(input) && MQTT.getValue().equalsIgnoreCase(output)) {
                log.info("Starting Kafka to MQTT bridge...");
                executor.submit(() -> {
                    try {
                        consumeKafka(config, queue);
                    } catch (ConnectException e) {
                        log.error("Connection error in Kafka consumer thread", e);
                    } catch (Exception e) {
                        log.error("Error in Kafka consumer thread", e);
                    }
                });

                var mqttProducer = new MqttProducer(config);
                mqttProducer.connect();
                executor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            produceMqttMessage(mqttProducer, queue);
                        }
                    } catch (Exception e) {
                        log.error("Error in MQTT producer thread", e);
                    } finally {
                        log.info("Closing MQTT producer...");
                        mqttProducer.close();
                    }
                });
            } else {
                log.error("Unsupported input/output combination: input={}, output={}", input, output);
                System.exit(1);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down bridge executor...");
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                        log.warn("Executor did not terminate in time after shutdownNow.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Shutdown hook interrupted while waiting for termination.", e);
                }
                log.info("Bridge executor shut down.");
            }));

        } catch (Exception e) {
            log.error("Failed to start bridge due to an unexpected error", e);
            System.exit(1);
        }
    }

    /**
     * Connects and subscribes the MQTT consumer.
     * Assumes the subscribe() method is blocking or manages its own internal polling thread.
     *
     * @param config The application configuration.
     * @param queue The queue to offer consumed messages to.
     */
    private void consumeMqtt(final AppConfig config, final LinkedBlockingQueue<String> queue) {
        log.info("Connecting MQTT consumer...");
        var consumer = new MqttConsumer(config);
        try {
            consumer
                .connect()
                .subscribe(queue::offer);
            log.info("MQTT consumer connected and subscribed.");
        } catch (ConnectException e) {
            log.error("Connection error publishing Kafka message", e);
        } catch (Exception e) {
            log.error("Failed to connect or subscribe MQTT consumer", e);
        } finally {
            log.info("todo");
            // Consumer close logic depends on MqttConsumer's lifecycle.
            // If subscribe() blocks, consumer.close() should be called when subscribe() returns.
            // If subscribe() returns immediately, consumer.close() might be needed here or in a shutdown hook.
            // For now, assuming MqttConsumer manages its own close on thread termination or JVM shutdown.
        }
    }

    /**
     * Connects and subscribes the Kafka consumer.
     * Assumes the subscribe() method is blocking or manages its own internal polling thread.
     *
     * @param config The application configuration.
     * @param queue The queue to offer consumed messages to.
     * @throws ConnectException If there's a problem connecting to Kafka.
     */
    private void consumeKafka(final AppConfig config, final LinkedBlockingQueue<String> queue) throws ConnectException {
        log.info("Connecting Kafka consumer...");
        var consumer = new KafkaConsumer(config);
        try {
            consumer
                .connect()
                .subscribe(queue::offer);
            log.info("Kafka consumer connected and subscribed.");
        } catch (ConnectException e) {
            log.error("Failed to connect Kafka consumer", e);
        } catch (Exception e) {
            log.error("Failed to subscribe Kafka consumer", e);
        } finally {
            log.info("todo");
            // Similar considerations for KafkaConsumer.close()
        }
    }

    /**
     * Helper method to take a message from the queue and publish it using an already connected MqttProducer.
     * This method is called repeatedly by the producer thread.
     *
     * @param producer The connected MqttProducer instance.
     * @param queue The queue to take messages from.
     */
    private void produceMqttMessage(final MqttProducer producer, final BlockingQueue<String> queue) {
        try {
            String message = queue.take();
            log.debug("Producing MQTT message: {}", message);
            producer.publish(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("MQTT producer interrupted while taking from queue.", e);
        } catch (Exception e) {
            log.error("Error publishing MQTT message", e);
        }
    }

    /**
     * Helper method to take a message from the queue and publish it using an already connected KafkaProducer.
     * This method is called repeatedly by the producer thread.
     *
     * @param producer The connected KafkaProducer instance.
     * @param queue The queue to take messages from.
     */
    private void produceKafkaMessage(final KafkaProducer producer, final BlockingQueue<String> queue) {
        try {
            String message = queue.take();
            log.debug("Producing Kafka message: {}", message);
            producer.publish(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Kafka producer interrupted while taking from queue.", e);
        } catch (Exception e) {
            log.error("Error publishing Kafka message", e);
        }
    }
}
