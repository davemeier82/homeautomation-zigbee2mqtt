/*
 * Copyright 2021-2023 the original author or authors.
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

import io.github.davemeier82.homeautomation.core.device.Device;
import io.github.davemeier82.homeautomation.core.device.property.AbstractAlarm;
import io.github.davemeier82.homeautomation.core.device.property.AlarmState;
import io.github.davemeier82.homeautomation.core.event.EventPublisher;
import io.github.davemeier82.homeautomation.core.event.factory.EventFactory;
import io.github.davemeier82.homeautomation.core.mqtt.MqttClient;

public class Zigbee2MqttAlarm extends AbstractAlarm {

  private final String topic;
  private final MqttClient mqttClient;

  public Zigbee2MqttAlarm(int id,
                          Device device,
                          String topic,
                          EventPublisher eventPublisher,
                          EventFactory eventFactory,
                          MqttClient mqttClient
  ) {
    super(id, device, eventPublisher, eventFactory);
    this.topic = topic;
    this.mqttClient = mqttClient;
  }

  public void setAlarmState(String zigbeeState) {
    AlarmState alarmState = switch (zigbeeState.toLowerCase()) {
      case "clear" -> AlarmState.OFF;
      case "pre-alarm" -> AlarmState.PRE_ALARM;
      case "fire" -> AlarmState.FIRE;
      case "burglar" -> AlarmState.BURGLAR;
      case "silenced" -> AlarmState.SILENCED;
      default -> throw new IllegalStateException("alarm state " + zigbeeState + " not supported");
    };
    setAlarmState(alarmState);
  }

  @Override
  public void setState(AlarmState state) {
    String zigbeeState = switch (state) {
      case OFF -> "stop";
      case PRE_ALARM -> "pre_alarm";
      case FIRE -> "fire";
      case BURGLAR -> "burglar";
      case SILENCED -> "silenced";
    };
    mqttClient.publish(topic + "/set", "{ \"alarm\": \"" + zigbeeState + "\" } ");
  }
}
