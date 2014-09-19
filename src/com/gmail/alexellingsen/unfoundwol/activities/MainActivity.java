package com.gmail.alexellingsen.unfoundwol.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.gmail.alexellingsen.unfoundwol.LazyAdapter;
import com.gmail.alexellingsen.unfoundwol.R;
import com.gmail.alexellingsen.unfoundwol.Settings;
import com.gmail.alexellingsen.unfoundwol.devices.Device;
import com.gmail.alexellingsen.unfoundwol.devices.Devices;
import com.gmail.alexellingsen.unfoundwol.dialogs.DeviceDialog;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends Activity {

    private Devices devices;
    private LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getThemeID(this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devices = new Devices(getApplicationContext());
        devices.loadDevices();

        setupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                return DeviceDialog.show(this);
            case R.id.action_settings:
                Intent i = new Intent(this, PrefsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.setHeaderTitle(adapter.getItem(info.position).getName());

            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        String itemText = adapter.getItem(info.position).getName();

        switch (item.getItemId()) {
            case R.id.action_edit:
                DeviceDialog.show(this, devices.get(itemText));
                break;
            case R.id.action_remove:
                adapter.remove(adapter.getItem(info.position));
                devices.remove(itemText);
                devices.saveDevices();
                break;
        }

        return true;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listview);

        list.setEmptyView(empty);
    }

    public void addDevice(String name, String host, String mac, int port) {
        Device d = new Device(name, host, mac, port);

        devices.put(name, d);
        devices.saveDevices();

        adapter.add(d);
    }

    public void editDevice(String name, String newName, String host, String mac, int port) {
        Device d = devices.get(name);

        d.setName(newName);
        d.setHost(host);
        d.setMac(mac);
        d.setPort(port);

        devices.saveDevices();

        adapter.notifyDataSetChanged();
    }

    public boolean containsDevice(String name) {
        return devices.containsKey(name);
    }

    private void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        registerForContextMenu(listview);

        TextView emptyView = new TextView(this);

        emptyView.setText(getString(R.string.devices_none));

        listview.setEmptyView(emptyView);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                String item = ((TextView) view.findViewById(R.id.list_item_name)).getText().toString();

                if (devices.containsKey(item)) {
                    Device d = devices.get(item);

                    if (d.canWake()) {
                        devices.get(item).wake();
                        Toast.makeText(context, getString(R.string.magic_packet_sending, item), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, getString(R.string.device_info_error, item), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.device_not_found, item), Toast.LENGTH_LONG).show();
                }
            }
        });

        adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(devices.getDevices());
    }

}
