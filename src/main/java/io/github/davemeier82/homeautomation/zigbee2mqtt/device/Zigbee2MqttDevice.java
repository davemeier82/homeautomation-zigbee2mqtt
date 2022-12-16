/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.davemeier82.homeautomation.zigbee2mqtt.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.davemeier82.homeautomation.core.device.mqtt.DefaultMqttSubscriber;
import io.github.davemeier82.homeautomation.core.device.property.DeviceProperty;
import io.github.davemeier82.homeautomation.core.device.property.defaults.*;
import io.github.davemeier82.homeautomation.core.event.DataWithTimestamp;
import io.github.davemeier82.homeautomation.core.event.EventPublisher;
import io.github.davemeier82.homeautomation.core.event.factory.EventFactory;
import io.github.davemeier82.homeautomation.core.mqtt.MqttClient;
import io.github.davemeier82.homeautomation.zigbee2mqtt.Zigbee2MqttMessage;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.property.Zigbee2MqttRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Device for Zigbee2Mqtt devices (<a href="https://www.zigbee2mqtt.io/">Zigbee2Mqtt</a>)
 *
 * @author David Meier
 * @since 0.1.0
 */
public class Zigbee2MqttDevice extends DefaultMqttSubscriber {
  private static final Logger log = LoggerFactory.getLogger(Zigbee2MqttDevice.class);
  public static final String MQTT_TOPIC = "zigbee2mqtt";
  public static final String TYPE = "zigbee2mqtt";

  private final String id;
  private final String baseTopic;
  private final ObjectMapper objectMapper;
  private final DefaultBatteryStateSensor batteryStateSensor;
  private final DefaultIlluminanceSensor illuminanceSensor;
  private final DefaultTemperatureSensor temperatureSensor;
  private final DefaultHumiditySensor humiditySensor;
  private final DefaultMotionSensor motionSensor;
  private final Zigbee2MqttRelay relay;
  private final DefaultWindowSensor windowSensor;

  /**
   * Constructor.
   *
   * @param id                the id
   * @param displayName       the display name
   * @param objectMapper      the object mapper to map the MQTT payload
   * @param eventPublisher    the event publisher
   * @param eventFactory      the event factory
   * @param customIdentifiers optional custom identifiers
   */
  public Zigbee2MqttDevice(String id,
                           String displayName,
                           ObjectMapper objectMapper,
                           EventPublisher eventPublisher,
                           EventFactory eventFactory,
                           MqttClient mqttClient,
                           Map<String, String> customIdentifiers
  ) {
    super(displayName, customIdentifiers);
    this.id = id;
    baseTopic = MQTT_TOPIC + "/" + id;
    this.objectMapper = objectMapper;
    batteryStateSensor = new DefaultBatteryStateSensor(0, this, eventPublisher, eventFactory);
    illuminanceSensor = new DefaultIlluminanceSensor(1, this, eventPublisher, eventFactory);
    temperatureSensor = new DefaultTemperatureSensor(2, this, eventPublisher, eventFactory);
    humiditySensor = new DefaultHumiditySensor(3, this, eventPublisher, eventFactory);
    motionSensor = new DefaultMotionSensor(4, this, eventPublisher, eventFactory);
    relay = new Zigbee2MqttRelay(5, this, baseTopic, eventPublisher, eventFactory, mqttClient);
    windowSensor = new DefaultWindowSensor(6, this, false, eventPublisher, eventFactory);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<? extends DeviceProperty> getDeviceProperties() {
    return List.of(batteryStateSensor, illuminanceSensor, temperatureSensor, humiditySensor, motionSensor, relay, windowSensor);
  }

  @Override
  public String getTopic() {
    return baseTopic;
  }

  @Override
  public void processMessage(String topic, Optional<ByteBuffer> payload) {
    payload.ifPresent(byteBuffer -> {
      String message = UTF_8.decode(byteBuffer).toString();
      log.debug("{}: {}", topic, message);
      try {
        Zigbee2MqttMessage zigbee2MqttMessage = objectMapper.readValue(message, Zigbee2MqttMessage.class);
        if (zigbee2MqttMessage.getBattery() != null) {
          batteryStateSensor.setBatteryLevel(zigbee2MqttMessage.getBattery());
        }
        if (zigbee2MqttMessage.getIlluminanceLux() != null) {
          illuminanceSensor.setIlluminanceInLux(zigbee2MqttMessage.getIlluminanceLux());
        }
        if (zigbee2MqttMessage.getTemperature() != null) {
          temperatureSensor.setTemperatureInDegree(zigbee2MqttMessage.getTemperature());
        }
        if (zigbee2MqttMessage.getHumidity() != null) {
          humiditySensor.setRelativeHumidityInPercent(zigbee2MqttMessage.getHumidity());
        }
        if (zigbee2MqttMessage.getState() != null) {
          relay.setRelayStateTo(zigbee2MqttMessage.getState().equalsIgnoreCase("ON"));
        }
        if (zigbee2MqttMessage.getOccupancy() != null) {
          motionSensor.setMotionDetected(new DataWithTimestamp<>(ZonedDateTime.now(), zigbee2MqttMessage.getOccupancy()));
        }
        if (zigbee2MqttMessage.getContact() != null) {
          windowSensor.setIsOpen(!zigbee2MqttMessage.getContact());
        }
      } catch (JsonProcessingException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
}
