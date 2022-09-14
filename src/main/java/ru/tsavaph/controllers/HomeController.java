package ru.tsavaph.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tsavaph.devices.Device;
import ru.tsavaph.devices.DeviceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.tsavaph.devices.DeviceType.DEVICE_MAP;
import static ru.tsavaph.devices.DeviceType.devices;

/**
 * Home page controller
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String showHome(Model model) {

        model.addAttribute("device", new Device());
        model.addAttribute("typesOfDevices", DeviceType.DEVICE_TYPES);
        model.addAttribute("pvAddresses", getListWithAddresses(40001, 40049));
        model.addAttribute("llAddresses", getListWithAddresses(40051, 40099));
        model.addAttribute("hhAddresses", getListWithAddresses(40101, 40149));

        return "home";
    }

    @RequestMapping(value="/", method=RequestMethod.POST, params="action=add")
    public String submitFormAdd(@ModelAttribute("device") Device device) {

        for (Map.Entry<String, Device> deviceEntry : devices.entrySet()) {
            Device s =  deviceEntry.getValue();

            if (s.getModbusHhAddress() == device.getModbusHhAddress() ||
                    s.getModbusLlAddress() == device.getModbusLlAddress() ||
                    s.getModbusPvAddress() == device.getModbusPvAddress()) {
                return "address-is-occupied";
            }
        }
        String deviceEui = getDeviceEui(device);

        device.setDeviceEui(deviceEui);
        devices.put(deviceEui, device);

        System.out.println(device);
        return "success";
    }

    @RequestMapping(value="/", method=RequestMethod.POST, params="action=delete")
    public String submitFormDelete(@ModelAttribute("device") Device device) {

        String deviceEui = getDeviceEui(device);

        if (devices.remove(deviceEui) == null) {
            return "nothing-to-delete";
        } else {
            return "deleted";
        }
    }

    /**
     * Gets device EUI from DEVICE_MAP from {@link DeviceType} using device type information
     *
     * @param device selected device
     * @return device EUI
     */
    private String getDeviceEui(Device device) {
        String deviceEui = DEVICE_MAP.entrySet()
                .stream()
                .filter(entry -> device.getDeviceType().equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().get();

        return deviceEui;
    }

    /**
     * Fills an {@link ArrayList} with addresses with a step 2 numbers
     *
     * @param first first address
     * @param last last address
     * @return list with addresses
     */
    private List<String> getListWithAddresses(int first, int last) {
        List<String> addresses = new ArrayList<>();
        for (int i = first; i < last + 1; i = i + 2) {
            addresses.add(Integer.toString(i));
        }
        return addresses;
    }
}