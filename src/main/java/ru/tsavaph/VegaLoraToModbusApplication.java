package ru.tsavaph;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.tsavaph.modbus.ModbusServerDataHandler;
import ru.tsavaph.vega.VegaLoraWebSocketClient;

/**
 * Simple application for extracting data of devices from Vega-Lora Server
 * and sending data to selected holding registers by creating Modbus Server.
 * Device type, EUI and parsing are set in a recourses/files/devices_data.csv file.
 * <p>
 * Data is being pulled once in pull time from a recourses/files/devices_data.csv file.
 * @author Anton Tsygansky (tsavaph)
 */
@SpringBootApplication
public class VegaLoraToModbusApplication {
	public static void main(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(VegaLoraToModbusApplication.class, args);
		Resource resource = new ClassPathResource("files/server_parameters.csv");

		// get inputStream object
		InputStream inputStream;
		try {
			inputStream = resource.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String[] serverParameters;
		// get a csv values array
		try {
			serverParameters = new CSVReaderHeaderAware(new InputStreamReader(inputStream)).readAll().get(0);
		} catch (IOException | CsvException e) {
			throw new RuntimeException(e);
		}

		if (serverParameters.length != 5) {
			throw new RuntimeException(new Exception(
					"TYPE THESE PARAMETERS 'VEGA_LORA_ADDRESS, LOGIN, PASSWORD, WEB_SOCKET_PULL_TIME, MODBUS_PULL_TIME' into recourses/files/server_parameters.csv file"
			));
		}
		int websocketPullTime = Integer.parseInt(serverParameters[3]);
		int modbusPullTime = Integer.parseInt(serverParameters[4]);



		String uri = serverParameters[0];
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			System.out.println("URI should be like this \"ws://server.iotvega.com/ws\" or this \"ws://192.12.11.10:8000\"");
			throw new RuntimeException(e);
		}

		String login = serverParameters[1];
		String password = serverParameters[2];
		// start websocket pulling
		VegaLoraWebSocketClient client = new VegaLoraWebSocketClient(uri, login, password, websocketPullTime);
		// open websocket
		client.connect();
		client.pull();


		// start modbus server
		ModbusServerDataHandler modbusServerDataHandler = new ModbusServerDataHandler(modbusPullTime);
		modbusServerDataHandler.Listen();
		modbusServerDataHandler.handleData();
	}
}