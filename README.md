# LoraVegaToModbusServer
#### This application is used for extracting device data from [IOT Vega Server](https://iotvega.com/soft/server) and sending process value (PV), low limit (LL) and high limit (LL) data to created Modbus Server
###### To test its functionality you can use [demo server](http://server.iotvega.com/index.html) provided by [VEGA Absolute team](https://en.iotvega.com):

| Demo server address        |  Login  | Password |
|----------------------------|:-------:|:--------:|
| ws://server.iotvega.com/ws |  demo   |   demo   |

###### To test Modbus Server you can use [OpenModBusTool](https://github.com/heX16/OpenModbusTool)

## Building the application
#### Type a command `mvn clean install`

### Launching the application
* First you have to install Java JRE

  * FOR LINUX UBUNTU
      * Open terminal, type `sudo apt install openjdk-11-jre` and follow the instructions
      * Reboot the system
      * Open terminal, type `java -version` and ensure that java version is 11

  * FOR WINDOWS*
      * Download and install [Java JRE 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

* Then open command line in the target folder with LoraVegaToModbusServer-0.1.1.war file
* Type `java -jar LoraVegaToModbusServer-0.1.1 VEGA_LORA_ADDRESS LOGIN PASSWORD` (for example: `java -jar LoraVegaToModbusServer-0.1.1.war ws://server.iotvega.com/ws demo demo`)
* Open a browser, type `127.0.0.1:8080` and do the magic