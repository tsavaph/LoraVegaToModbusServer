# LoraVegaToModbusServer

* First you have to install Java JRE

* FOR LINUX
	* Open terminal, type 'sudo apt install openjdk-11-jre' and folow the instructions
	* Reboot the system
	* Open terminal, type 'java -version' and ensure that java version is 11

* FOR WINDOWS*
	* Download and install Java JRE 11

* Then open command line in the target folder with a metran-0.1.1-SNAPSHOT.war file
* Type 'java -jar LoraVegaToModbusServer-0.1.1 VEGA_LORA_ADDRESS LOGIN PASSWORD' (for example: 'java -jar LoraVegaToModbusServer-0.1.1.war ws://server.iotvega.com/ws demo demo')
* Open browser, type 127.0.0.1:8080 and do the magic
