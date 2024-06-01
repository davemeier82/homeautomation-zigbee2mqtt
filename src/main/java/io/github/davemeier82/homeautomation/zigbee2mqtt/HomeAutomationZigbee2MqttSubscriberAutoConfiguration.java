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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.davemeier82.homeautomation.core.repositories.DeviceRepository;
import io.github.davemeier82.homeautomation.core.updater.AlarmStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.BatteryLevelUpdateService;
import io.github.davemeier82.homeautomation.core.updater.CloudBaseValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.Co2ValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.HumidityValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.IlluminanceValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.MotionStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.PressureValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.RainIntervalValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.RainRateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.RainTodayValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.RelayStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.SmokeStateValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.TemperatureValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.UvIndexValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindDirectionValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindGustDirectionValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindGustSpeedValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindRunValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindSpeedValueUpdateService;
import io.github.davemeier82.homeautomation.core.updater.WindowStateValueUpdateService;
import io.github.davemeier82.homeautomation.spring.core.HomeAutomationCorePersistenceAutoConfiguration;
import io.github.davemeier82.homeautomation.spring.core.HomeAutomationCoreValueUpdateServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({HomeAutomationCoreValueUpdateServiceAutoConfiguration.class, HomeAutomationCorePersistenceAutoConfiguration.class, JacksonAutoConfiguration.class})
public class HomeAutomationZigbee2MqttSubscriberAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean({ObjectMapper.class, TemperatureValueUpdateService.class, HumidityValueUpdateService.class, PressureValueUpdateService.class, CloudBaseValueUpdateService.class,
      RainIntervalValueUpdateService.class, RainTodayValueUpdateService.class, RainRateValueUpdateService.class, IlluminanceValueUpdateService.class, UvIndexValueUpdateService.class,
      WindSpeedValueUpdateService.class, WindDirectionValueUpdateService.class, WindGustSpeedValueUpdateService.class, WindGustDirectionValueUpdateService.class, WindRunValueUpdateService.class,
      DeviceRepository.class, Zigbee2MqttDeviceFactory.class})
  Zigbee2MqttSubscriber zigbee2MqttSubscriber(ObjectMapper objectMapper,
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
    return new Zigbee2MqttSubscriber(objectMapper, temperatureValueUpdateService, humidityValueUpdateService, batteryLevelUpdateService, illuminanceValueUpdateService, motionStateValueUpdateService,
        relayStateValueUpdateService, windowStateValueUpdateService, smokeStateValueUpdateService, co2ValueUpdateService, alarmStateValueUpdateService, deviceRepository, zigbee2MqttDeviceFactory);
  }

}
