package com.gmail.alexellingsen.unfoundwol.devices;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.gmail.alexellingsen.unfoundwol.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class Devices {

    public static final String TABLE_NAME = "devices";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_HOST = "host";
    public static final String KEY_PORT = "port";
    public static final String KEY_MAC = "mac";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_HOST + " TEXT, "
            + KEY_PORT + " INTEGER, "
            + KEY_MAC + " TEXT" + ");";

    private Context context;
    private DatabaseHandler _db;
    private SortedMap<String, Device> devices = new TreeMap<String, Device>();

    public Devices(Context context) {
        this.context = context;
    }

    public boolean containsKey(String name) {
        return devices.containsKey(name);
    }

    public Device get(String name) {
        return devices.get(name);
    }

    public Device[] getDevices() {
        ArrayList<Device> devices = new ArrayList<Device>();

        for (Entry<String, Device> entry : this.devices.entrySet()) {
            devices.add(entry.getValue());
        }

        return devices.toArray(new Device[devices.size()]);
    }

    public void loadDevices() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        for (String device : sharedPref.getStringSet("devices", new HashSet<String>())) {
            String[] args = device.split(";");

            String name = args[0];
            String host = args[1];
            String mac = args[2];
            int port = Integer.parseInt(args[3]);

            devices.put(args[0], new Device(name, host, mac, port));
        }
    }

    public Device put(String name, Device device) {
        return devices.put(name, device);
    }

    public boolean remove(String name) {
        return !(devices.remove(name) == null);
    }

    public void saveDevices() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();

        HashSet<String> set = new HashSet<String>();

        for (Entry<String, Device> entry : devices.entrySet()) {
            set.add(entry.getValue().serialize());
        }

        editor.putStringSet("devices", set);

        editor.commit();
    }

}