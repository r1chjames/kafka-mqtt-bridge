package kafkamqttbridge.mqtt;

import kafkamqttbridge.AppConfig;
import kafkamqttbridge.AppProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.hivemq.client.mqtt.datatypes.MqttQos.EXACTLY_ONCE;
import static kafkamqttbridge.AppConfigConstants.*;

@Slf4j
@AllArgsConstructor
public class MqttProducer extends MqttUtils implements AppProducer {

    private AppConfig config;

    public MqttProducer connect() {
        getMqttClient()
            .connectWith()
            .simpleAuth()
            .username(config.get(MQTT_USERNAME))
            .password(config.get(MQTT_PASSWORD).getBytes())
            .applySimpleAuth()
            .send()
            .whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    log.error("Unable to connect to MQTT broker", throwable.getCause());
//                    throw new ConnectException("Unable to connect to MQTT broker");
                }
            });
        return this;
    }

    public boolean publish(final String payload) {
        var topic = config.get(MQTT_TOPIC);
        final AtomicBoolean success = new AtomicBoolean(false);
        getMqttClient()
            .publishWith()
            .topic(topic)
            .payload(payload.getBytes())
            .qos(EXACTLY_ONCE)
            .send()
            .whenComplete((mqtt3Publish, throwable) -> {
                if (throwable != null) {
                    log.error("Unable to publish to topic {} MQTT broker", topic, throwable.getCause());
                } else {
                    success.set(true);
                }
            });
        return success.get();
    }
}
