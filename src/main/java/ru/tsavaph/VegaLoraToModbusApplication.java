package ru.tsavaph;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvException;
import de.re.easymodbus.modbusclient.ModbusClient;
import de.re.easymodbus.server.ModbusServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.tsavaph.devices.Device;
import ru.tsavaph.devices.DeviceType;
import ru.tsavaph.vega.VegaLoraWebSocketClient;

import static ru.tsavaph.vega.VegaLoraWebSocketClient.devices;

/**
 * Simple application for extracting data of devices from Vega-Lora Server
 * and sending data to selected holding registers by creating Modbus Server.
 * Device type, EUI and parsing are set in {@link DeviceType}.
 * <p>
 * Data is being pulled once in PULL_TIME field.
 * @author Anton Tsygansky (tsavaph)
 */
@SpringBootApplication
public class VegaLoraToModbusApplication {

	/**
	 * Value that sets a pulling time.
	 */
	private static final int PULL_TIME = 10;

	public static void main(String[] args) {
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

		if (serverParameters.length != 3) {
			throw new RuntimeException(new Exception(
					"TYPE THESE PARAMETERS 'VEGA_LORA_ADDRESS, LOGIN, PASSWORD' into recourses/files/server_parameters.csv file"
			));
		}

		try {
			new URI(serverParameters[0]);
		} catch (URISyntaxException e) {
			System.out.println("URI should be like this \"ws://server.iotvega.com/ws\" or this \"ws://192.12.11.10:8000\"");
			throw new RuntimeException(e);
		}

		// starts a thread with modbus server and setting holding registers for devices
		new Thread(() -> {
			try {
				ModbusServer modbusServer = new ModbusServer();

				modbusServer.Listen();
				while (true) {
					TimeUnit.SECONDS.sleep(PULL_TIME);
					for (Device device : devices.values()) {
						System.out.println(device);
						try {
							int f = -40_000;
							int pvAddress = device.getModbusPvAddress() + f;
							float pv = Float.parseFloat(device.getPvValue());
							int[] pvHoldingRegister = ModbusClient.ConvertFloatToTwoRegisters(pv);
							int llAddress = device.getModbusLlAddress() + f;
							float ll = Float.parseFloat(device.getLlValue());
							int[] llHoldingRegister = ModbusClient.ConvertFloatToTwoRegisters(ll);
							int hhAddress = device.getModbusHhAddress() + f;
							float hh = Float.parseFloat(device.getHhValue());
							int[] hhHoldingRegister = ModbusClient.ConvertFloatToTwoRegisters(hh);
							modbusServer.holdingRegisters[pvAddress] = pvHoldingRegister[0];
							modbusServer.holdingRegisters[pvAddress + 1] = pvHoldingRegister[1];
							modbusServer.holdingRegisters[llAddress] = llHoldingRegister[0];
							modbusServer.holdingRegisters[llAddress + 1] = llHoldingRegister[1];
							modbusServer.holdingRegisters[hhAddress] = hhHoldingRegister[0];
							modbusServer.holdingRegisters[hhAddress + 1] = hhHoldingRegister[1];

						} catch (NullPointerException | NumberFormatException e) {
							System.out.println(e);
						}
					}
				}
			} catch (InterruptedException | IOException e) {
				System.out.println(e);
			}
		}).start();

		// starts a thread with WebSocket and polling data
		new Thread(() -> {
			// http://server.iotvega.com/index.html "ws://server.iotvega.com/ws"
			String uri = serverParameters[0];
			try {
				VegaLoraWebSocketClient client = new VegaLoraWebSocketClient(uri);

				String login = serverParameters[1];
				String password = serverParameters[2];
				// open websocket
				client.connect(login, password);

				while (true) {

					if (client.isClosed())
						client.connect(login, password);

					for (Device device : devices.values()) {
						JSONObject obj1 = new JSONObject();
						obj1.put("cmd", "get_data_req");
						obj1.put("devEui", device.getDeviceEui());
						obj1.put("select", new JSONObject().put("limit", 1));
						String message = obj1.toString();
						client.send(message);
					}
					TimeUnit.SECONDS.sleep(PULL_TIME);
				}
			} catch (URISyntaxException e) {
				System.out.println(e);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}).start();
	}
}