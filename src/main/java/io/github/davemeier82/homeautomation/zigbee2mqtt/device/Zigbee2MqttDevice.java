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
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.property.Zigbee2MqttAlarm;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.property.Zigbee2MqttRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparingLong;

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
  private final DefaultSmokeSensor smokeSensor;
  private final DefaultCo2Sensor co2Sensor;
  private final Zigbee2MqttAlarm alarm;

  private final Set<DeviceProperty> deviceProperties = ConcurrentHashMap.newKeySet();

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
    int i = 0;
    batteryStateSensor = new DefaultBatteryStateSensor(i++, this, eventPublisher, eventFactory);
    illuminanceSensor = new DefaultIlluminanceSensor(i++, this, eventPublisher, eventFactory);
    temperatureSensor = new DefaultTemperatureSensor(i++, this, eventPublisher, eventFactory);
    humiditySensor = new DefaultHumiditySensor(i++, this, eventPublisher, eventFactory);
    motionSensor = new DefaultMotionSensor(i++, this, eventPublisher, eventFactory);
    relay = new Zigbee2MqttRelay(i++, this, baseTopic, eventPublisher, eventFactory, mqttClient);
    windowSensor = new DefaultWindowSensor(i++, this, false, eventPublisher, eventFactory);
    smokeSensor = new DefaultSmokeSensor(i++, this, eventPublisher, eventFactory);
    co2Sensor = new DefaultCo2Sensor(i++, this, eventPublisher, eventFactory);
    alarm = new Zigbee2MqttAlarm(i, this, baseTopic, eventPublisher, eventFactory, mqttClient);
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
    return deviceProperties.stream().sorted(comparingLong(DeviceProperty::getId)).toList();
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
          deviceProperties.add(batteryStateSensor);
          batteryStateSensor.setBatteryLevel(zigbee2MqttMessage.getBattery());
        }
        if (zigbee2MqttMessage.getIlluminanceLux() != null) {
          deviceProperties.add(illuminanceSensor);
          illuminanceSensor.setIlluminanceInLux(zigbee2MqttMessage.getIlluminanceLux());
        }
        if (zigbee2MqttMessage.getTemperature() != null) {
          deviceProperties.add(temperatureSensor);
          temperatureSensor.setTemperatureInDegree(zigbee2MqttMessage.getTemperature());
        }
        if (zigbee2MqttMessage.getHumidity() != null) {
          deviceProperties.add(humiditySensor);
          humiditySensor.setRelativeHumidityInPercent(zigbee2MqttMessage.getHumidity());
        }
        if (zigbee2MqttMessage.getState() != null) {
          deviceProperties.add(relay);
          relay.setRelayStateTo(zigbee2MqttMessage.getState().equalsIgnoreCase("ON"));
        }
        if (zigbee2MqttMessage.getOccupancy() != null) {
          deviceProperties.add(motionSensor);
          motionSensor.setMotionDetected(new DataWithTimestamp<>(ZonedDateTime.now(), zigbee2MqttMessage.getOccupancy()));
        }
        if (zigbee2MqttMessage.getContact() != null) {
          deviceProperties.add(windowSensor);
          windowSensor.setIsOpen(!zigbee2MqttMessage.getContact());
        }
        if (zigbee2MqttMessage.getCo2() != null) {
          deviceProperties.add(co2Sensor);
          co2Sensor.setCo2LevelInPpm(zigbee2MqttMessage.getCo2());
        }
        if (zigbee2MqttMessage.getCo2() != null) {
          deviceProperties.add(co2Sensor);
          co2Sensor.setCo2LevelInPpm(zigbee2MqttMessage.getCo2());
        }
        if (zigbee2MqttMessage.getSmoke() != null) {
          deviceProperties.add(smokeSensor);
          smokeSensor.setSmokeDetected(zigbee2MqttMessage.getSmoke());
        }
        if (zigbee2MqttMessage.getSirenState() != null) {
          deviceProperties.add(alarm);
          alarm.setAlarmState(zigbee2MqttMessage.getSirenState());
        }
      } catch (JsonProcessingException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
}
