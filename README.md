# DCU Projects - C++ and Java Applications

This repository contains two key projects developed as part of the **DCU coursework**. The projects were written in **C++** and **Java**, each focusing on different aspects of system programming, device interfacing, and network communication.

## 1. Java GUI Client/Server Application (EE402)
This project is a **Java-based GUI Client/Server application** designed to simulate asynchronous sensor metrics aggregation. It was developed to fulfill the requirements of the **EE402** module.

### Features:
- **Client-Server Architecture**: Implements a client/server model where multiple clients asynchronously send sensor data to the server.
- **GUI Interface**: The application provides a graphical interface for user interaction.
- **Asynchronous Processing**: The server aggregates sensor metrics from multiple clients in an asynchronous manner, simulating real-world scenarios of data collection from various sensors.

### Technologies:
- Java
- Swing (for GUI)
- Socket Programming

## 2. C++ OOP API for I2C Devices and MQTT Communication (EE513)
This project, developed for the **EE513** module, consists of two main components:
1. **Custom C++ API** for interfacing with I2C devices.
2. **MQTT Application** demonstrating communication between publisher and subscriber nodes.

### Features:

#### 2.1 C++ Custom API for I2C Devices:
- **Devices Interfaced**: 
  - **DS3231 RTC Module**: Used to read and write time-related data.
  - **ADXL345 Accelerometer**: Used to fetch acceleration data.
- **Low-Level Interaction**: The API provides direct control over the devices by manipulating registers and address buffers to enable communication.
- **OOP Design**: The API is structured using object-oriented principles, making it easy to extend and maintain.

#### 2.2 MQTT Communication (EE513-2):
- **MQTT Protocol**: A lightweight messaging protocol for small sensors and mobile devices optimized for low-bandwidth usage.
- **Publisher/Subscriber Example**: Implements two MQTT nodes (one publisher and one subscriber) communicating with a **paho-MQTT** server running on a **VirtualBox Debian instance**.
- **Use Case**: Simulates real-world scenarios where devices publish sensor data and other devices subscribe to receive updates in real time.

### Libraries and Requirements:
- I2C Protocol.
- MQTT (paho-MQTT).
- VirtualBox with Debian instance.
