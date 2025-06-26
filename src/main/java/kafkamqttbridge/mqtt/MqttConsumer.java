package kafkamqttbridge.mqtt;

import kafkamqttbridge.AppConfig;
import kafkamqttbridge.AppConsumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.function.Consumer;

import static kafkamqttbridge.AppConfigConstants.*;

@Slf4j
@AllArgsConstructor
public final class MqttConsumer extends MqttUtils implements AppConsumer {

    private AppConfig config;

    @Override
    public MqttConsumer connect() throws ConnectException {
        getMqttClient(config)
            .connectWith()
            .simpleAuth()
            .username(config.getString(MQTT_USERNAME))
            .password(config.getString(MQTT_PASSWORD).getBytes())
            .applySimpleAuth()
            .send()
            .whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    log.error("Unable to connect to MQTT broker", throwable.getCause());
                }
            });
        return this;
    }

    @Override
    public void subscribe(final Consumer<String> messageConsumer) {
        var topic = config.getString(MQTT_TOPIC);
        getMqttClient(config)
            .subscribeWith()
            .topicFilter(topic)
            .callback(payload -> messageConsumer.accept(payload.getPayload().toString()))
            .send()
            .whenComplete((subAck, throwable) -> {
                if (throwable != null) {
                    log.error("Unable to subscribe to topic {} on MQTT broker", topic, throwable.getCause());
                }
            });
    }
}
