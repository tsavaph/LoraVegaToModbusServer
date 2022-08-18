package ru.tsavaph;

import de.re.easymodbus.modbusclient.ModbusClient;
import de.re.easymodbus.server.ModbusServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;
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
		ConfigurableApplicationContext ctx = SpringApplication.run(VegaLoraToModbusApplication.class, args);

		for (String s : args) {
			System.out.println(s);
		}

		if (args.length != 3) {
			ctx.close();
			throw new RuntimeException(new Exception(
					"USE THIS COMMAND 'java -jar LoraVegaToModbusServer-0.1.1.war VEGA_LORA_ADDRESS LOGIN PASSWORD'"
			));
		}

		try {
			URI uri = new URI(args[1]);
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
							int f = -40_000 - 1;
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
			String uri = args[0];
			try {
				VegaLoraWebSocketClient client = new VegaLoraWebSocketClient(uri);

				String login = args[1];
				String password = args[2];
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
				System.out.println(e);;
			}
		}).start();
	}
}