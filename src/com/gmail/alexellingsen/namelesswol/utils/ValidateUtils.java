package com.gmail.alexellingsen.namelesswol.utils;

import com.gmail.alexellingsen.namelesswol.devices.Device;

import java.net.InetAddress;

/**
 * Collection of validation methods.
 */
public class ValidateUtils {

    /**
     * Validates given host address by trying to parse it to a {@link java.net.InetAddress}.
     *
     * @param host The host address to validate.
     * @return Returns {@code true} if the host address is valid, {@code false} otherwise.
     */
    public static boolean validateHost(String host) {
        try {
            InetAddress.getByName(host);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    /**
     * Validates given MAC address.
     *
     * @param mac The MAC address to validate.
     * @return Returns {@code true} if the MAC address is valid, {@code false} otherwise.
     */
    public static boolean validateMAC(final String mac) {
        try {
            Device.validateMac(mac);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates given port number, checking if it's too low or too high.
     *
     * @param port The port to validate.
     * @return Returns {@code true} if the port is valid, {@code false} otherwise.
     */
    public static boolean validatePort(int port) {
        return port >= 1 && port <= 65535;
    }
}
