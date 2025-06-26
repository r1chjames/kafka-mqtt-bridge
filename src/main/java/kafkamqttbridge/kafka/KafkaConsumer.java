package kafkamqttbridge.kafka;

import kafkamqttbridge.AppConfig;
import kafkamqttbridge.AppConsumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
public final class KafkaConsumer implements AppConsumer {

    private AppConfig config;

    @Override
    public KafkaConsumer connect() throws ConnectException {
        return null;
    }

    @Override
    public void subscribe(final Consumer<String> messageConsumer) {

    }
}
