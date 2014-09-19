package com.gmail.alexellingsen.unfoundwol;

import com.gmail.alexellingsen.unfoundwol.devices.Device;

import java.net.InetAddress;

public class ValidateUtils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean validateHost(String host) {
        try {
            InetAddress.getByName(host);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public static boolean validateMAC(final String mac) {
        try {
            Device.validateMac(mac);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean validatePort(int port) {
        return port >= 1 && port <= 65535;
    }

}