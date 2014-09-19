package com.gmail.alexellingsen.unfoundwol.devices;

import android.os.AsyncTask;
import android.util.Log;
import com.gmail.alexellingsen.unfoundwol.Common;
import com.gmail.alexellingsen.unfoundwol.ValidateUtils;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device {

    private String host;
    private String mac;
    private String name;
    private int port;

    public Device(String name, String host, String mac, int port) {
        this.name = name;
        this.host = host;
        this.mac = mac;
        this.port = port;
    }

    public boolean canWake() {
        if (host.isEmpty() || mac.isEmpty() || ValidateUtils.validatePort(port))
            return false;

        boolean validHost;

        try {
            URI uri = new URI("my://" + host + ":" + port);

            if (uri.getHost() == null | uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(), "URI must have host and port parts.");
            }

            validHost = true;
        } catch (URISyntaxException ex) {
            validHost = false;
        }

        return validHost && ValidateUtils.validateMAC(mac);
    }

    public String getHost() {
        return host;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return this.port;
    }

    public static String[] validateMac(String mac) throws IllegalArgumentException {
        // Error handle semi colons
        mac = mac.replace(";", ":");

        // Attempt to assist the user a little
        String newMac = "";

        if (mac.matches("([a-zA-Z0-9]){12}")) {
            // Expand 12 chars into a valid mac address
            for (int i = 0; i < mac.length(); i++) {
                if ((i > 1) && (i % 2 == 0)) {
                    newMac += ":";
                }
                newMac += mac.charAt(i);
            }
        } else {
            newMac = mac;
        }

        // RegExp pattern match a valid MAC address
        final Pattern pat = Pattern.compile("((([0-9a-fA-F]){2}[-:]){5}([0-9a-fA-F]){2})");
        final Matcher m = pat.matcher(newMac);

        if (m.find()) {
            String result = m.group();
            return result.split("(\\:|\\-)");
        } else {
            throw new IllegalArgumentException("Invalid MAC address");
        }
    }

    public String serialize() {
        return this.name + ";" + this.host + ";" + this.mac + ";" + this.port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean wake() {
        try {
            return new WakeAsyncTask().execute().get();
        } catch (Exception e) {
            return false;
        }
    }

    private class WakeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... arg0) {
            if (mac == null)
                return false;

            try {
                String[] hex = validateMac(mac);

                // Convert to base16 bytes
                byte[] macBytes = new byte[6];

                for (int i = 0; i < 6; i++) {
                    macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
                }

                byte[] bytes = new byte[6 + 16 * macBytes.length];

                // Fill first 6 bytes
                for (int i = 0; i < 6; i++) {
                    bytes[i] = (byte) 0xff;
                }

                // Fill remaining bytes with MAC
                for (int i = 6; i < bytes.length; i += macBytes.length) {
                    System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
                }

                InetAddress address = InetAddress.getByName(host);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
                DatagramSocket socket = new DatagramSocket();

                socket.send(packet);
                socket.close();

                Log.d(Common.TAG, "Wake-on-LAN packet sent.");
                return true;
            } catch (Exception e) {
                Log.e(Common.TAG, "Failed to send Wake-on-LAN packet: " + e.toString());
                return false;
            }
        }
    }
}