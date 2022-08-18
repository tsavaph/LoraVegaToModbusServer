package ru.tsavaph.vega;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.tsavaph.devices.Device;
import ru.tsavaph.devices.DeviceType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Vega Lora Web Socket Client
 */
public class VegaLoraWebSocketClient extends WebSocketClient {
    public static volatile HashMap<String, Device> devices = new HashMap<>();

    /**
     * Creates a Web Socket Client
     * @param uri WS URI
     * @throws URISyntaxException
     */
    public VegaLoraWebSocketClient(String uri) throws URISyntaxException {
        super(new URI(uri));
    }

    /**
     * Connects to Vega Lora Server
     * @param login username
     * @param password password
     */
    public void connect(String login, String password) {

        super.connect();
        JSONObject obj = new JSONObject();
        obj.put("cmd", "auth_req");
        obj.put("login", login);
        obj.put("password", password);
        send(obj.toString());
    }

    /**
     * Action on receiving a message: pull server and gets the data
     * @param message received message
     */
    @Override
    public void onMessage(String message) {

        System.out.println(message);
        JSONObject obj = new JSONObject(message);

        if (obj.has("devEui")) {
            String devEui = obj.getString("devEui");
            System.out.println("devEui " + devEui);
            JSONArray dataList = obj.getJSONArray("data_list");
            JSONObject dataObj = dataList.getJSONObject(0);
            String data = dataObj.getString("data");
            String[] values = DeviceType.parseData(devEui, data);
            devices.get(devEui).setPvValue(values[0]);
            devices.get(devEui).setLlValue(values[1]);
            devices.get(devEui).setHhValue(values[2]);
            System.out.println("data " + data);
        }
    }

    /**
     * Action on when connection opens
     */
    @Override
    public void onOpen(ServerHandshake handshake) {

        System.out.println("opened connection");
    }

    /**
     * Action on when connection closses
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {

        System.out.println("closed connection");
    }

    /**
     * Action on when connection has an error
     */
    @Override
    public void onError(Exception e) {

        e.printStackTrace();
    }

    /**
     * Send a message to the server
     * @param message message what being sent to the server
     */
    @Override
    public  void send(String message) {

        if (! (super.isClosed() || super.isClosing())) {
            while (!super.isOpen()) {
                // wait
            }
            super.send(message);
        }
    }
}
