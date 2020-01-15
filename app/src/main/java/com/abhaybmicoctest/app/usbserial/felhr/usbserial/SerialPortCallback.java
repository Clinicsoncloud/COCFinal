package com.abhaybmicoctest.app.usbserial.felhr.usbserial;

import java.util.List;


public interface SerialPortCallback {
    void onSerialPortsDetected(List<UsbSerialDevice> serialPorts);
}
