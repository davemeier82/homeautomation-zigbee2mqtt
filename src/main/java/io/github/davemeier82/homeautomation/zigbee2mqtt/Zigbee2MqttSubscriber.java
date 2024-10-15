/*
 * Copyright 2021-2024 the original author or authors.
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

package io.github.davemeier82.homeautomation.zigbee2mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.davemeier82.homeautomation.core.device.Device;
import io.github.davemeier82.homeautomation.core.device.DeviceId;
import io.github.davemeier82.homeautomation.core.device.mqtt.MqttSubscriber;
import io.github.davemeier82.homeautomation.core.device.property.AlarmState;
import io.github.davemeier82.homeautomation.core.device.property.DevicePropertyId;
import io.github.davemeier82.homeautomation.core.repositories.DeviceRepository;
import io.github.davemeier82.homeautomation.core.updater.AlarmStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.BatteryLevelUpdateService;
import io.github.davemeier82.homeautomation.core.updater.Co2ValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.HumidityValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.IlluminanceValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.MotionStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.RelayStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.SmokeStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.TemperatureValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindowStateValueUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDeviceType.ZIGBEE_2_MQTT;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Zigbee2MqttSubscriber implements MqttSubscriber {
  public static final String MQTT_TOPIC = "zigbee2mqtt";
  private static final Logger log = LoggerFactory.getLogger(Zigbee2MqttSubscriber.class);
  private final ObjectMapper objectMapper;
  private final TemperatureValueUpdateService temperatureValueUpdateService;
  private final HumidityValueUpdateService humidityValueUpdateService;
  private final BatteryLevelUpdateService batteryLevelUpdateService;
  private final IlluminanceValueUpdateService illuminanceValueUpdateService;
  private final MotionStateValueUpdateService motionStateValueUpdateService;
  private final RelayStateValueUpdateService relayStateValueUpdateService;
  private final WindowStateValueUpdateService windowStateValueUpdateService;
  private final SmokeStateValueUpdateService smokeStateValueUpdateService;
  private final Co2ValueUpdateService co2ValueUpdateService;
  private final AlarmStateValueUpdateService alarmStateValueUpdateService;
  private final DeviceRepository deviceRepository;
  private final Zigbee2MqttDeviceFactory zigbee2MqttDeviceFactory;

  public Zigbee2MqttSubscriber(ObjectMapper objectMapper,
                               TemperatureValueUpdateService temperatureValueUpdateService,
                               HumidityValueUpdateService humidityValueUpdateService,
                               BatteryLevelUpdateService batteryLevelUpdateService,
                               IlluminanceValueUpdateService illuminanceValueUpdateService,
                               MotionStateValueUpdateService motionStateValueUpdateService,
                               RelayStateValueUpdateService relayStateValueUpdateService,
                               WindowStateValueUpdateService windowStateValueUpdateService,
                               SmokeStateValueUpdateService smokeStateValueUpdateService,
                               Co2ValueUpdateService co2ValueUpdateService,
                               AlarmStateValueUpdateService alarmStateValueUpdateService,
                               DeviceRepository deviceRepository,
                               Zigbee2MqttDeviceFactory zigbee2MqttDeviceFactory
  ) {
    this.objectMapper = objectMapper;
    this.temperatureValueUpdateService = temperatureValueUpdateService;
    this.humidityValueUpdateService = humidityValueUpdateService;
    this.batteryLevelUpdateService = batteryLevelUpdateService;
    this.illuminanceValueUpdateService = illuminanceValueUpdateService;
    this.motionStateValueUpdateService = motionStateValueUpdateService;
    this.relayStateValueUpdateService = relayStateValueUpdateService;
    this.windowStateValueUpdateService = windowStateValueUpdateService;
    this.smokeStateValueUpdateService = smokeStateValueUpdateService;
    this.co2ValueUpdateService = co2ValueUpdateService;
    this.alarmStateValueUpdateService = alarmStateValueUpdateService;
    this.deviceRepository = deviceRepository;
    this.zigbee2MqttDeviceFactory = zigbee2MqttDeviceFactory;
  }

  @Override
  public String getTopic() {
    return MQTT_TOPIC + "/#";
  }

  @Override
  public void processMessage(String topic, Optional<ByteBuffer> payload) {
    String[] topicParts = topic.split("/");
    if (topicParts.length < 2 || topicParts[1].equals("bridge")) {
      log.debug("ignoring message for topic {}", topic);
      return;
    }
    payload.ifPresent(byteBuffer -> {
      String message = UTF_8.decode(byteBuffer).toString();
      log.debug("{}: {}", topic, message);
      DeviceId deviceId = new DeviceId(topicParts[1], ZIGBEE_2_MQTT);
      deviceRepository.getByDeviceId(deviceId).orElseGet(() -> {
        Device newDevice = zigbee2MqttDeviceFactory.createDevice(deviceId.type(), deviceId.id(), deviceId.toString(), Map.of(), Map.of()).orElseThrow();
        deviceRepository.save(newDevice);
        return newDevice;
      });
      try {
        Zigbee2MqttMessage zigbee2MqttMessage = objectMapper.readValue(message, Zigbee2MqttMessage.class);
        if (zigbee2MqttMessage.getBattery() != null) {
          batteryLevelUpdateService.setValue(zigbee2MqttMessage.getBattery(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "battery"), deviceId + ": Battery Level");
        }
        if (zigbee2MqttMessage.getIlluminanceLux() != null) {
          illuminanceValueUpdateService.setValue(zigbee2MqttMessage.getIlluminanceLux(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "illumination"), deviceId + ": Illumination");
        }
        if (zigbee2MqttMessage.getTemperature() != null) {
          temperatureValueUpdateService.setValue(zigbee2MqttMessage.getTemperature().floatValue(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "temperature"), deviceId + ": Temperature");
        }
        if (zigbee2MqttMessage.getHumidity() != null) {
          humidityValueUpdateService.setValue(zigbee2MqttMessage.getHumidity().floatValue(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "humidity"), deviceId + ": Humidity");
        }
        if (zigbee2MqttMessage.getState() != null) {
          boolean isOn = zigbee2MqttMessage.getState().equalsIgnoreCase("ON");
          relayStateValueUpdateService.setValue(isOn, OffsetDateTime.now(), new DevicePropertyId(deviceId, "relay"), deviceId + ": Relay");
        }
        if (zigbee2MqttMessage.getOccupancy() != null) {
          motionStateValueUpdateService.setValue(zigbee2MqttMessage.getOccupancy(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "motion"), deviceId + ": Motion State");
        }
        if (zigbee2MqttMessage.getContact() != null) {
          windowStateValueUpdateService.setValue(!zigbee2MqttMessage.getContact(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "window"), deviceId + ": Window State");
        }
        if (zigbee2MqttMessage.getCo2() != null) {
          co2ValueUpdateService.setValue(zigbee2MqttMessage.getCo2(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "co2"), deviceId + ": Co2");
        }
        if (zigbee2MqttMessage.getSmoke() != null) {
          smokeStateValueUpdateService.setValue(zigbee2MqttMessage.getSmoke(), OffsetDateTime.now(), new DevicePropertyId(deviceId, "smoke"), deviceId + ": Smoke State");
        }
        if (zigbee2MqttMessage.getSirenState() != null) {
          alarmStateValueUpdateService.setValue(toAlarmState(zigbee2MqttMessage.getSirenState()), OffsetDateTime.now(), new DevicePropertyId(deviceId, "alarm"), deviceId + ": Alarm State");
        }
      } catch (JsonProcessingException e) {
        throw new UncheckedIOException(e);
      }
    });
  }

  public AlarmState toAlarmState(String zigbeeState) {
    return switch (zigbeeState.toLowerCase()) {
      case "clear" -> AlarmState.OFF;
      case "pre-alarm" -> AlarmState.PRE_ALARM;
      case "fire" -> AlarmState.FIRE;
      case "burglar" -> AlarmState.BURGLAR;
      case "silenced" -> AlarmState.SILENCED;
      default -> throw new IllegalStateException("alarm state " + zigbeeState + " not supported");
    };
  }
}
