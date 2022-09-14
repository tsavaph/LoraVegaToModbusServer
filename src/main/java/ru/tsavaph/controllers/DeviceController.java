package ru.tsavaph.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tsavaph.devices.Device;
import java.util.ArrayList;
import java.util.List;

import static ru.tsavaph.devices.DeviceType.devices;

/**
 * Page with devices list controller
 */
@Controller
public class DeviceController {

    @GetMapping("/devices")
    public String devices(Model model) {

        List<Device> deviceList = new ArrayList<>(devices.values());
        model.addAttribute(deviceList);

        return "devices";
    }
}