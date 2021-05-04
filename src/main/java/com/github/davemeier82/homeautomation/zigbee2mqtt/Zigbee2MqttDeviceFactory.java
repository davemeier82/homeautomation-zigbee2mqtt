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

package com.github.davemeier82.homeautomation.zigbee2mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davemeier82.homeautomation.core.device.DeviceId;
import com.github.davemeier82.homeautomation.core.device.mqtt.MqttDeviceFactory;
import com.github.davemeier82.homeautomation.core.device.mqtt.MqttSubscriber;
import com.github.davemeier82.homeautomation.core.event.EventFactory;
import com.github.davemeier82.homeautomation.core.event.EventPublisher;
import com.github.davemeier82.homeautomation.core.mqtt.MqttClient;
import com.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class Zigbee2MqttDeviceFactory implements MqttDeviceFactory {
  private static final Logger log = LoggerFactory.getLogger(Zigbee2MqttDeviceFactory.class);
  private final EventPublisher eventPublisher;
  private final EventFactory eventFactory;
  private final MqttClient mqttClient;
  private final ObjectMapper objectMapper;

  public Zigbee2MqttDeviceFactory(EventPublisher eventPublisher,
                                  EventFactory eventFactory,
                                  MqttClient mqttClient,
                                  ObjectMapper objectMapper
  ) {
    this.eventPublisher = eventPublisher;
    this.eventFactory = eventFactory;
    this.mqttClient = mqttClient;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean supportsDeviceType(String type) {
    return type.equals(Zigbee2MqttDevice.TYPE);
  }

  @Override
  public Set<String> getSupportedDeviceTypes() {
    return Set.of(Zigbee2MqttDevice.TYPE);
  }

  @Override
  public MqttSubscriber createDevice(String type, String id, String displayName) {
    Zigbee2MqttDevice device = new Zigbee2MqttDevice(id, displayName, objectMapper, eventPublisher, eventFactory);
    log.debug("creating Zigbee2Mqtt device with id {} ({})", id, displayName);
    mqttClient.subscribe(device.getTopic(), device::processMessage);
    eventPublisher.publishEvent(eventFactory.createNewDeviceCreatedEvent(device));

    return device;
  }

  @Override
  public String getRootTopic() {
    return Zigbee2MqttDevice.MQTT_TOPIC + "/";
  }

  @Override
  public Optional<DeviceId> getDeviceId(String topic) {
    String[] parts = topic.split("/");
    if (parts.length < 2) {
      return Optional.empty();
    }
    String id = parts[1];
    if (id.equals("bridge")) {
      return Optional.empty();
    }
    return Optional.of(new DeviceId(id, Zigbee2MqttDevice.TYPE));
  }

  @Override
  public Optional<MqttSubscriber> createMqttSubscriber(DeviceId deviceId) {
    try {
      return Optional.of(createDevice(deviceId.getType(), deviceId.getId(), deviceId.toString()));
    } catch (IllegalArgumentException e) {
      log.debug("unknown device with id: {}", deviceId);
      return Optional.empty();
    }
  }
}
