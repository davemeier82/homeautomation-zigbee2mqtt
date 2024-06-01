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

import io.github.davemeier82.homeautomation.core.mqtt.MqttClient;
import io.github.davemeier82.homeautomation.spring.core.HomeAutomationCoreMqttAutoConfiguration;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDeviceTypeFactory;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.property.Zigbee2MqttAlarmDevicePropertyController;
import io.github.davemeier82.homeautomation.zigbee2mqtt.device.property.Zigbee2MqttRelayDevicePropertyController;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({HomeAutomationCoreMqttAutoConfiguration.class})
public class HomeAutomationZigbee2MqttAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  Zigbee2MqttDeviceFactory zigbee2MqttDeviceFactory() {
    return new Zigbee2MqttDeviceFactory();
  }


  @Bean
  @ConditionalOnMissingBean
  Zigbee2MqttDeviceTypeFactory zigbee2MqttDeviceTypeFactory() {
    return new Zigbee2MqttDeviceTypeFactory();
  }


  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(MqttClient.class)
  Zigbee2MqttAlarmDevicePropertyController zigbee2MqttAlarmDevicePropertyController(MqttClient mqttClient) {
    return new Zigbee2MqttAlarmDevicePropertyController(mqttClient);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(MqttClient.class)
  Zigbee2MqttRelayDevicePropertyController zigbee2MqttRelayDevicePropertyController(MqttClient mqttClient) {
    return new Zigbee2MqttRelayDevicePropertyController(mqttClient);
  }
}
