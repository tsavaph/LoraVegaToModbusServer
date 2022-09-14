package ru.tsavaph.devices;

import lombok.AllArgsConstructor;

/**
 * Used for extracting float values from a package with data
 */
@AllArgsConstructor
class Value {
    private int biasIndex;
    private int sizeIndex;
    private int divisionIndex;
    private String dataString;
    private String[] arrayWithValuesFromLinesOfCsvFile;

    /**
     * Extracts float values from a package with data of device
     * @return calculated value
     */
    float getValue() {
        int[] data = hexStringToArray(dataString);
        int bias = Integer.parseInt(arrayWithValuesFromLinesOfCsvFile[biasIndex]);
        int size = Integer.parseInt(arrayWithValuesFromLinesOfCsvFile[sizeIndex]);
        int division = Integer.parseInt(arrayWithValuesFromLinesOfCsvFile[divisionIndex]);

        return calculateValueFromData(data, bias, size, division);
    }

    /**
     * Calculates value from a data
     * @param data Int data array from a data string
     * @param bias - Byte bias
     * @param size - Number of bytes to get a hex value
     * @param division - Division for a value from the data
     * @return Calculated value from a data
     */
    private float calculateValueFromData(int[] data, int bias, int size, int division) {
        StringBuilder hexString = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            hexString.append(Integer.toHexString(data[bias + i]));
        }

        return Integer.parseInt(hexString.toString(), 16) / division;
    }

    /**
     * Converts HEX string to an int array
     *
     * @param dataString Data as a HEX string
     * @return an array with byte presented as integers
     */
    private int[] hexStringToArray(String dataString) {
        int[] data = new int[dataString.length() / 2];

        for (int i = 0; i < data.length; i++) {
            int index = i * 2;
            data[i] = Integer.parseInt(dataString.substring(index, index + 2), 16);
        }

        return data;
    }
}
