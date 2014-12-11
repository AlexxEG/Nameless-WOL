package com.gmail.alexellingsen.nameless_wol;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gmail.alexellingsen.nameless_wol.devices.Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("ConstantConditions")
public class LazyAdapter extends BaseAdapter {

    private ArrayList<Device> data;
    private static LayoutInflater inflater = null;

    public LazyAdapter(Activity a) {
        this.data = new ArrayList<>();

        if (data != null) {
            this.data.addAll(data);
        }

        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Device device) {
        data.add(device);

        Collections.sort(data, new Comparator<Device>() {
            @Override
            public int compare(Device d1, Device d2) {
                return d1.getName().compareTo(d2.getName());
            }
        });

        this.notifyDataSetChanged();
    }

    public void addAll(Collection<Device> devices) {
        this.data.addAll(devices);
        this.notifyDataSetChanged();
    }

    public Device find(int id) {
        for (Device device : data) {
            if (device.getID() == id)
                return device;
        }
        return null;
    }

    public int getCount() {
        return data.size();
    }

    public Device getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceView deviceView;

        if (convertView == null) {
            deviceView = (DeviceView) inflater.inflate(R.layout.list_item_device, null);
        } else {
            deviceView = (DeviceView) convertView;
        }

        deviceView.showDevice(getItem(position));
        return deviceView;
    }

    public void remove(Device device) {
        data.remove(device);
        this.notifyDataSetChanged();
    }

}
