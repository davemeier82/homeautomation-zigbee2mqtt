# homeautomation-zigbee2mqtt

This is an extension of the [homeautomation-spring-core](https://github.com/davemeier82/homeautomation-spring-core/blob/main/README.md) to add [Zigbee2MQTT devices](https://www.zigbee2mqtt.io/)
support.

Features

* REST Interface to trigger events (e.g. turn on light)
* REST Interface to configure the system (e.g. add/update/delete devices)

## Usage

Checkout the detailed usage in the Demo: [homeautomation-demo](https://github.com/davemeier82/homeautomation-demo/blob/main/README.md)

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.davemeier82.homeautomation</groupId>
            <artifactId>homeautomation-bom</artifactId>
            <version>${homeautomation-bom.version}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
<dependency>
    <groupId>io.github.davemeier82.homeautomation</groupId>
    <artifactId>homeautomation-spring-core</artifactId>
</dependency>
<dependency>
    <groupId>io.github.davemeier82.homeautomation</groupId>
    <artifactId>homeautomation-zigbee2mqtt</artifactId>
</dependency>
</dependencies>
```

## Supported device properties

| Property        | Events           | Controller                    |
|-----------------|------------------|-------------------------------|
| battery         | BatteryState     |                               |
| illuminance_lux | RelayState       |                               |
| temperature     | TemperatureValue |                               |
| humidity        | HumidityValue    |                               |
| state           | RelayState       | RelayDevicePropertyController |
| occupancy       | MotionState      |                               |
| contact         | WindowState      |                               |
| co2             | Co2Value         |                               |
| smoke           | SmokeState       |                               |
| siren_state     | AlarmState       | AlarmDevicePropertyController |