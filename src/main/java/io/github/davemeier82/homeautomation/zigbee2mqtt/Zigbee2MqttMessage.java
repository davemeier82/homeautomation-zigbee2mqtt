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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload of a Zigbee2Mqtt message.
 *
 * @author David Meier
 * @since 0.1.0
 */
public class Zigbee2MqttMessage {
  private Integer battery; // %
  @JsonProperty("illuminance_lux")
  private Integer illuminanceLux;
  private Integer linkquality; // lqi
  private Boolean occupancy;
  private Integer temperature; // C
  private Integer humidity; // %
  private Integer voltage; // V
  private Integer energy; // kWh
  private Integer current; // A
  private Integer power; // W
  private String state; // ON, OFF, TOGGLE
  private Integer brightness; // 0 - 254
  private Boolean contact;
  @JsonProperty("battery_low")
  private Boolean lowBattery;
  private Boolean tamper;
  private Boolean smoke;

  public Integer getBattery() {
    return battery;
  }

  public void setBattery(Integer battery) {
    this.battery = battery;
  }

  public Integer getIlluminanceLux() {
    return illuminanceLux;
  }

  public void setIlluminanceLux(Integer illuminanceLux) {
    this.illuminanceLux = illuminanceLux;
  }

  public Integer getLinkquality() {
    return linkquality;
  }

  public void setLinkquality(Integer linkquality) {
    this.linkquality = linkquality;
  }

  public Boolean getOccupancy() {
    return occupancy;
  }

  public void setOccupancy(Boolean occupancy) {
    this.occupancy = occupancy;
  }

  public Integer getTemperature() {
    return temperature;
  }

  public void setTemperature(Integer temperature) {
    this.temperature = temperature;
  }

  public Integer getHumidity() {
    return humidity;
  }

  public void setHumidity(Integer humidity) {
    this.humidity = humidity;
  }

  public Integer getVoltage() {
    return voltage;
  }

  public void setVoltage(Integer voltage) {
    this.voltage = voltage;
  }

  public Integer getEnergy() {
    return energy;
  }

  public void setEnergy(Integer energy) {
    this.energy = energy;
  }

  public Integer getCurrent() {
    return current;
  }

  public void setCurrent(Integer current) {
    this.current = current;
  }

  public Integer getPower() {
    return power;
  }

  public void setPower(Integer power) {
    this.power = power;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Integer getBrightness() {
    return brightness;
  }

  public void setBrightness(Integer brightness) {
    this.brightness = brightness;
  }

  public Boolean getContact() {
    return contact;
  }

  public void setContact(Boolean contact) {
    this.contact = contact;
  }

  public Boolean getLowBattery() {
    return lowBattery;
  }

  public void setLowBattery(Boolean lowBattery) {
    this.lowBattery = lowBattery;
  }

  public Boolean getTamper() {
    return tamper;
  }

  public void setTamper(Boolean tamper) {
    this.tamper = tamper;
  }

  public Boolean getSmoke() {
    return smoke;
  }

  public void setSmoke(Boolean smoke) {
    this.smoke = smoke;
  }
}
