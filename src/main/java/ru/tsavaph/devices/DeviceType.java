package ru.tsavaph.devices;

import java.util.*;

/**
 * Device type is used for setting app the application functionality.
 * To add one more device you have to update this class.
 */
public abstract class DeviceType {

    /**
     * Map with device EUI and its type (name).
     * Update this map to add a new device.
     */
    public static Map<String, String> DEVICE_MAP = Map.of(
            "3739343554376004", "TD-11 temperature of water 2d flour",
            "343438355B37650E", "TP-11 current loop 2d flour"
    );

    /**
     * List with device types
     */
    public static List<String> DEVICE_TYPES = new ArrayList<>(DEVICE_MAP.values());

    /**
     * Converts HEX string to data from device. Update case statement in the method while adding a new device.
     *
     * @param deviceEui The device EUI
     * @param dataString Data as a HEX string
     * @return an array with data[process value, low limit, high limit].
     */
    public static String[] parseData(String deviceEui, String dataString) {

        String[] parsedData = {
                "NO DATA",
                "NO DATA",
                "NO DATA"
        };

        int[] data;
        int packageType;
        int battery;
        int limit;
        int time;
        float pv;
        float ll;
        float hh;
        int packageReason;
        int inputStatus;

        switch (deviceEui) {

            // TD-11
            // example of data respond 015b00f0eef962fe000a28000d
            case "3739343554376004":
                data = hexStringToArray(dataString);

                packageType = data[0];
                battery = data[1];
                limit = data[2];

                time = Integer.parseInt(
                        Integer.toHexString(data[6]) +
                                Integer.toHexString(data[5]) +
                                Integer.toHexString(data[4]) +
                                Integer.toHexString(data[3]),
                        16);

                pv = Integer.parseInt(
                        Integer.toHexString(data[8]) +
                                Integer.toHexString(data[7]),
                        16) / 10;

                ll = data[9];
                hh = data[10];
                packageReason = data[11];
                inputStatus = data[12];

                parsedData = new String[]{
                        String.valueOf(pv),
                        String.valueOf(ll),
                        String.valueOf(hh)
                };
                break;

            // TP-11
            // example of data respond 016200152af9621b4c0414050000fb04
            case "343438355B37650E":
                data = hexStringToArray(dataString);
                packageType = data[0];
                battery = data[1];
                limit = data[2];

                time = Integer.parseInt(
                        Integer.toHexString(data[6]) +
                                Integer.toHexString(data[5]) +
                                Integer.toHexString(data[4]) +
                                Integer.toHexString(data[3]),
                        16);
                int deviceTemperature = data[7];

                ll = Integer.parseInt(
                        Integer.toHexString(data[9]) +
                                Integer.toHexString(data[8]),
                        16) / 100;

                hh = Integer.parseInt(
                        Integer.toHexString(data[11]) +
                                Integer.toHexString(data[10]),
                        16) / 100;

                packageReason = data[12];
                inputStatus = data[13];

                pv = Integer.parseInt(
                        Integer.toHexString(data[15]) +
                                Integer.toHexString(data[14]),
                        16) / 100;

                parsedData = new String[]{
                        String.valueOf(pv),
                        String.valueOf(ll),
                        String.valueOf(hh)
                };
                break;
        }

        return parsedData;
    }

    /**
     * Converts HEX string to an int array
     *
     * @param dataString Data as a HEX string
     * @return an array with byte presented as integers
     */
    private static int[] hexStringToArray(String dataString) {
        int[] data = new int[dataString.length() / 2];

        for (int i = 0; i < data.length; i++) {
            int index = i * 2;
            data[i] = Integer.parseInt(dataString.substring(index, index + 2), 16);
        }

        return data;
    }
}
