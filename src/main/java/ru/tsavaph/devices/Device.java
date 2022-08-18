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
    private String deviceType = "UNKNOWN";
    private String deviceEui = "UNKNOWN";
    private int modbusPvAddress = 40001;
    private int modbusHhAddress = 40051;
    private int modbusLlAddress = 40101;
    private String pvValue = "NO DATA";
    private String hhValue = "NO DATA";
    private String llValue = "NO DATA";
}
