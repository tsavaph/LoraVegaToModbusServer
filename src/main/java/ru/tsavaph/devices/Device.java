package ru.tsavaph.devices;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Device with its data
 */
@Getter
@Setter
@ToString
public class Device {
    public static final String NO_DATA = "NO DATA";
    private String deviceType = "UNKNOWN";
    private String deviceEui = "UNKNOWN";
    private int modbusPvAddress = 40001;
    private int modbusHhAddress = 40051;
    private int modbusLlAddress = 40101;
    private String pvValue = NO_DATA;
    private String hhValue = NO_DATA;
    private String llValue = NO_DATA;
}
