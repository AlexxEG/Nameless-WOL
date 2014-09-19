package com.gmail.alexellingsen.unfoundwol.devices;

import android.os.AsyncTask;
import android.util.Log;
import com.gmail.alexellingsen.unfoundwol.Common;
import com.gmail.alexellingsen.unfoundwol.ValidateUtils;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device {

    private int _id;
    private String _host;
    private String _mac;
    private String _name;
    private int _port;

    public Device(String name, String host, int port, String mac) {
        this._name = name;
        this._host = host;
        this._mac = mac;
        this._port = port;
    }

    public boolean canWake() {
        if (_host.isEmpty() || _mac.isEmpty() || ValidateUtils.validatePort(_port))
            return false;

        boolean validHost;

        try {
            URI uri = new URI("my://" + _host + ":" + _port);

            if (uri.getHost() == null | uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(), "URI must have host and port parts.");
            }

            validHost = true;
        } catch (URISyntaxException ex) {
            validHost = false;
        }

        return validHost && ValidateUtils.validateMAC(_mac);
    }

    public int getID() {
        return _id;
    }

    public String getHost() {
        return _host;
    }

    public String getMac() {
        return _mac;
    }

    public String getName() {
        return _name;
    }

    public int getPort() {
        return _port;
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

    public void setID(int id) {
        _id = id;
    }

    public void setHost(String host) {
        _host = host;
    }

    public void setMac(String mac) {
        _mac = mac;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setPort(int port) {
        _port = port;
    }

    public boolean wake() {
        try {
            return new WakeAsyncTask().execute().get();
        } catch (Exception e) {
            return false;
        }
    }

    public String toString() {
        return String.format("Device (ID: %s, Name: %s, Host: %s, Port: %s, Mac: %s)", _id, _name, _host, _port, _mac);
    }

    private class WakeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... arg0) {
            if (_mac == null)
                return false;

            try {
                String[] hex = validateMac(_mac);

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

                InetAddress address = InetAddress.getByName(_host);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, _port);
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