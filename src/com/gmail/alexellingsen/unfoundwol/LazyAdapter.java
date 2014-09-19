package com.gmail.alexellingsen.unfoundwol;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gmail.alexellingsen.unfoundwol.devices.Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("ConstantConditions")
public class LazyAdapter extends BaseAdapter {

    private ArrayList<Device> data;
    private static LayoutInflater inflater = null;

    public LazyAdapter(Activity a) {
        this.data = new ArrayList<Device>();

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
        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.list_item_device, null);

        TextView text_name = (TextView) vi.findViewById(R.id.list_item_name);
        TextView text_host_port = (TextView) vi.findViewById(R.id.list_item_host_port);
        TextView text_mac = (TextView) vi.findViewById(R.id.list_item_mac);

        Device d = data.get(position);

        text_name.setText(d.getName());
        text_host_port.setText(d.getHost() + ":" + d.getPort());
        text_mac.setText(d.getMac());

        return vi;
    }

    public void remove(Device device) {
        data.remove(device);
        this.notifyDataSetChanged();
    }

}
