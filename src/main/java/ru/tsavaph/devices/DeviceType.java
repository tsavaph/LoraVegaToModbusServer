package ru.tsavaph.devices;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Device type is used for setting app the application functionality.
 * To add one more device you have to update this class.
 */
public abstract class DeviceType {

    /**
     * List with device data values from a csv file
     */
    private static final List<String[]> DEVICE_PARSING_CSV_FILE_STRINGS = initDeviceParsingCsvFileStrings();

    /**
     * Map with device EUI and its type (name).
     * Update this map to add a new device.
     */
    public static final Map<String, String> DEVICE_MAP = initDeviceMap();

    /**
     * List with device types
     */
    public static final List<String> DEVICE_TYPES = new ArrayList<>(DEVICE_MAP.values());

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

        float pv;
        float ll;
        float hh;

        for(String[] arrayWithValuesFromLinesOfCsvFile : DEVICE_PARSING_CSV_FILE_STRINGS) {

            if (arrayWithValuesFromLinesOfCsvFile[0].equals(deviceEui)) {
                Value pvValue = new Value(2, 3, 4, dataString, arrayWithValuesFromLinesOfCsvFile);
                pv = pvValue.getValue();

                Value llValue = new Value(5, 6, 7, dataString, arrayWithValuesFromLinesOfCsvFile);
                ll = llValue.getValue();

                Value hhValue = new Value(8, 9, 10, dataString, arrayWithValuesFromLinesOfCsvFile);
                hh = hhValue.getValue();

                parsedData = new String[]{
                        String.valueOf(pv),
                        String.valueOf(ll),
                        String.valueOf(hh)
                };
            }
        }
        return parsedData;
    }


    /**
     * Method extracts all data from a file devices_data.csv from recourses/files/devices_data.csv
     * @return parsed data from a file
     */
    private static List<String[]> initDeviceParsingCsvFileStrings() {
        // read a csv file with parsing data
        Resource resource = new ClassPathResource("files/devices_data.csv");

        // get inputStream object
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String[]> deviceParsingCsvFileStrings;
        // get a csv values array
        try {
            deviceParsingCsvFileStrings = new CSVReaderHeaderAware(new InputStreamReader(inputStream)).readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        return deviceParsingCsvFileStrings;
    }

    /**
     * Method to init devices from deviceParsingCsvFileStrings
     * @return map with devices EUI and its type
     */
    private static @NotNull Map<String, String> initDeviceMap() {
        Map<String, String> deviceMap = new HashMap<>();

        for (String[] cvsLine: DEVICE_PARSING_CSV_FILE_STRINGS) {
            deviceMap.put(cvsLine[0], cvsLine[1]);
        }

        return deviceMap;
    }
}
