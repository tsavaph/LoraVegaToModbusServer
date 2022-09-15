# LoraVegaToModbusServer
#### This application is used for extracting device data from [IOT Vega Server](https://iotvega.com/soft/server) and sending process value (PV), low limit (LL) and high limit (LL) data to created Modbus Server
###### To test its functionality you can use [demo server](http://server.iotvega.com/index.html) provided by [VEGA Absolute team](https://en.iotvega.com):

|     Demo server address      | Login | Password |
|:----------------------------:|:-----:|:--------:|
|  ws://server.iotvega.com/ws  | demo  |   demo   |

###### To test Modbus Server you can use [OpenModbusTool](https://github.com/heX16/OpenModbusTool)

## Video presentation of the application
https://user-images.githubusercontent.com/46519125/190412942-8bbb0fe2-8347-4984-a066-bfa21ad85299.mp4

## Setting up application parameters

### I) Devices Data
* Open `resources/files/devices_data.csv` file
* Type devices EUI, Device Type
* Type PV Bias,PV Size, PV Division
* Type LL Bias,LL Size,LL Division
* Type HH Bias,HH Size, HH Division
###### Bias, size and division information can be found in devices manuals
* ###### bias - byte bias from a data package starting from 0
* ###### size - number of bytes in a data package
* ###### division - number needed to divide the value

### II) Server Parameters
* Open `resources/files/server_parameters.csv` file
* Type Web Socket Address of IOT Vega Server
* Type Login of IOT Vega Server
* Type Password of IOT Vega Server
* Type Web Socket Pull Time
* Type Modbus Server Pull Time

## Building the application
* Download and install [Maven](https://maven.apache.org) 
* Open LoraVegaToModbusServer folder, open cmd and type a command `mvn clean install`

## Launching the application
* First you have to install Java JRE

  * FOR LINUX UBUNTU
      * Open terminal, type `sudo apt install openjdk-11-jre` and follow the instructions
      * Reboot the system
      * Open terminal, type `java -version` and ensure that java version is 11

  * FOR WINDOWS
      * Download and install [Java JRE 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

* Then open command line in the target folder with LoraVegaToModbusServer-0.1.1.war file
* For Windows type `java -jar LoraVegaToModbusServer-1.0.0.war`
* For Linux type `sudo java -jar LoraVegaToModbusServer-1.0.0.war`
* Open a browser, type `127.0.0.1:8080` or `localhost:8080` and do the magic

## Connection to the Modbus Server
* Set an IP address of your LAN Card
* Connect using port 502
