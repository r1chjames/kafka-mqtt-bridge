package kafkamqttbridge.kafka;

import kafkamqttbridge.AppConfig;
import kafkamqttbridge.AppProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class KafkaProducer implements AppProducer {

    private AppConfig config;

    @Override
    public KafkaProducer connect() {
        return null;
    }

    @Override
    public boolean publish(final String payload) {
        return true;
    }
}
