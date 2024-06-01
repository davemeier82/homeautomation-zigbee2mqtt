package io.github.davemeier82.homeautomation.zigbee2mqtt.device;

import io.github.davemeier82.homeautomation.core.device.DeviceType;
import io.github.davemeier82.homeautomation.core.device.mqtt.AbstractDevice;

import java.util.Map;

import static io.github.davemeier82.homeautomation.zigbee2mqtt.device.Zigbee2MqttDeviceType.ZIGBEE_2_MQTT;

public class Zigbee2MqttDevice extends AbstractDevice {
  private final String id;

  public Zigbee2MqttDevice(String id,
                           String displayName,
                           Map<String, String> customIdentifiers
  ) {
    super(displayName, customIdentifiers);
    this.id = id;
  }

  @Override
  public DeviceType getType() {
    return ZIGBEE_2_MQTT;
  }

  @Override
  public String getId() {
    return id;
  }

}