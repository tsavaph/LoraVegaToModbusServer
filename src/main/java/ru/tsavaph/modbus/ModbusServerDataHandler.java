package ru.tsavaph.modbus;

import de.re.easymodbus.modbusclient.ModbusClient;
import de.re.easymodbus.server.ModbusServer;
import lombok.AllArgsConstructor;
import ru.tsavaph.devices.Device;

import java.util.concurrent.TimeUnit;

import static ru.tsavaph.devices.DeviceType.devices;

/**
 * Modbus Server what handles devices data
 */
@AllArgsConstructor
public class ModbusServerDataHandler extends ModbusServer {
    private int pullTime;

    /**
     * Sends data to modbus server
     */
    public void handleData() {

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(pullTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (Device device : devices.values()) {
                    System.out.println(device);

                    if (device.getLlValue().equals(Device.NO_DATA) ||
                            device.getHhValue().equals(Device.NO_DATA) ||
                            device.getPvValue().equals(Device.NO_DATA)) {

                        System.out.println("waiting for data");
                    } else {
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
                        this.holdingRegisters[pvAddress] = pvHoldingRegister[0];
                        this.holdingRegisters[pvAddress + 1] = pvHoldingRegister[1];
                        this.holdingRegisters[llAddress] = llHoldingRegister[0];
                        this.holdingRegisters[llAddress + 1] = llHoldingRegister[1];
                        this.holdingRegisters[hhAddress] = hhHoldingRegister[0];
                        this.holdingRegisters[hhAddress + 1] = hhHoldingRegister[1];
                    }
                }
            }
        }).start();
    }

}
