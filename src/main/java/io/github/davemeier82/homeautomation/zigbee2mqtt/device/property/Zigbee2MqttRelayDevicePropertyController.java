/*
 * Copyright 2021-2022 the original author or authors.
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

package io.github.davemeier82.homeautomation.zigbee2mqtt.device.property;

import io.github.davemeier82.homeautomation.core.device.DeviceType;
import io.github.davemeier82.homeautomation.core.device.property.DevicePropertyId;
import io.github.davemeier82.homeautomation.core.device.property.RelayDevicePropertyController;
import io.github.davemeier82.homeautomation.core.mqtt.MqttClient;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDeviceType;

import java.util.Set;

import static io.github.davemeier82.homeautomation.zigbee2mqtt.Zigbee2MqttSubscriber.MQTT_TOPIC;

public class Zigbee2MqttRelayDevicePropertyController implements RelayDevicePropertyController {

  private static final Set<Zigbee2MqttDeviceType> DEVICE_TYPES = Set.of(Zigbee2MqttDeviceType.ZIGBEE_2_MQTT);
  private final MqttClient mqttClient;

  public Zigbee2MqttRelayDevicePropertyController(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
  }

  @Override
  public void turnOn(DevicePropertyId devicePropertyId) {
    mqttClient.publish(MQTT_TOPIC + "/" + devicePropertyId.deviceId().id() + "/set", """
        {
          "state": "ON"
        }
        """);
  }

  @Override
  public void turnOff(DevicePropertyId devicePropertyId) {
    mqttClient.publish(MQTT_TOPIC + "/" + devicePropertyId.deviceId().id() + "/set", """
        {
          "state": "OFF"
        }
        """);
  }

  @Override
  public Set<? extends DeviceType> getSupportedDeviceTypes() {
    return DEVICE_TYPES;
  }
}
