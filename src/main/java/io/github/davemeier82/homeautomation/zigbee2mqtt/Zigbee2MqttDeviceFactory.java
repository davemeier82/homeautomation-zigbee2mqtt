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

package io.github.davemeier82.homeautomation.zigbee2mqtt;

import io.github.davemeier82.homeautomation.core.device.Device;
import io.github.davemeier82.homeautomation.core.device.DeviceFactory;
import io.github.davemeier82.homeautomation.core.device.DeviceType;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDevice;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.stream;

public class Zigbee2MqttDeviceFactory implements DeviceFactory {
  private static final Logger log = LoggerFactory.getLogger(Zigbee2MqttDeviceFactory.class);

  @Override
  public boolean supportsDeviceType(DeviceType type) {
    return stream(Zigbee2MqttDeviceType.values()).anyMatch(t -> t == type);
  }

  @Override
  public Set<? extends DeviceType> getSupportedDeviceTypes() {
    return Set.of(Zigbee2MqttDeviceType.values());
  }

  @Override
  public Optional<Device> createDevice(DeviceType type, String id, String displayName, Map<String, String> parameters, Map<String, String> customIdentifiers) {
    if (supportsDeviceType(type)) {
      log.debug("creating Zigbee2Mqtt device with id {} ({})", id, displayName);
      return Optional.of(new Zigbee2MqttDevice(id, displayName, customIdentifiers));
    }
    return Optional.empty();
  }

}
