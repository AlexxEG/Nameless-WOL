package com.gmail.alexellingsen.namelesswol.activities;

import android.app.Activity;
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
import com.gmail.alexellingsen.namelesswol.LazyAdapter;
import com.gmail.alexellingsen.namelesswol.R;
import com.gmail.alexellingsen.namelesswol.devices.Device;
import com.gmail.alexellingsen.namelesswol.devices.Devices;
import com.gmail.alexellingsen.namelesswol.dialogs.DeviceDialog;
import com.gmail.alexellingsen.namelesswol.utils.Common;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends Activity {

    private LazyAdapter adapter;

    @Override
    public void onBackPressed() {
        if (getIntent().getAction().equals(Common.TASKER_ACTION_EDIT)) {
            this.setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Initialize SQLite database
        Devices.initialize(this);

        // Setup main ListView
        this.setupListView();
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

            // Set header title to selected device
            menu.setHeaderTitle(adapter.getItem(info.position).getName());

            // Inflate context menu
            getMenuInflater().inflate(R.menu.context_menu, menu);

            // Show 'Wake' in scenarios where tapping the list item selects it instead of waking it
            MenuItem sendItem = menu.findItem(R.id.action_wake);
            boolean enableSendItem = !getIntent().getAction().equals(Intent.ACTION_MAIN);

            sendItem.setEnabled(enableSendItem).setVisible(enableSendItem);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Device device = adapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.action_wake:
                this.wakeDevice(device);
                break;
            case R.id.action_edit:
                DeviceDialog.show(this, device);
                break;
            case R.id.action_remove:
                adapter.remove(adapter.getItem(info.position));
                Devices.delete(device);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void addDevice(String name, String host, int port, String mac) {
        Device device = new Device(name, host, port, mac);

        // Add device to database & set id from result.
        device.setID(Devices.add(device));

        adapter.add(device);
    }

    public void editDevice(int id, String name, String host, int port, String mac) {
        Device device = adapter.find(id);

        device.setName(name);
        device.setHost(host);
        device.setPort(port);
        device.setMac(mac);

        Devices.update(device);

        adapter.notifyDataSetChanged();
    }

    private void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        registerForContextMenu(listview);

        TextView emptyView = new TextView(this);

        emptyView.setText(getString(R.string.devices_none));

        listview.setEmptyView(emptyView);
        listview.setOnItemClickListener(getOnItemClickListener());

        adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(Devices.getAll());
    }

    public void wakeDevice(Device device) {
        String name = device.getName();

        if (device.canWake()) {
            if (device.wake()) {
                Toast.makeText(this, getString(R.string.magic_packet_sending, name), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.magic_packet_failed, name), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.device_info_error, name), Toast.LENGTH_LONG).show();
        }
    }

    private OnItemClickListener getOnItemClickListener() {
        if (getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            return new MainItemClickListener();
        } else if (getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT)) {
            return new ShortcutItemClickListener();
        } else if (getIntent().getAction().equals(Common.TASKER_ACTION_EDIT)) {
            return new TaskerItemClickListener();
        } else {
            return null;
        }
    }

    private class MainItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Device device = adapter.getItem(position);

            MainActivity.this.wakeDevice(device);
        }
    }

    private class ShortcutItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Device device = adapter.getItem(position);

            final Intent shortcutIntent = new Intent(MainActivity.this, WakeDeviceActivity.class);
            final Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(MainActivity.this, R.drawable.ic_launcher);
            final Intent intent = new Intent();

            shortcutIntent.putExtra(Common.INTENT_EXTRA_DEVICE_ID, device.getID());

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_text, device.getName()));
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

            setResult(RESULT_OK, intent);

            Toast.makeText(MainActivity.this, getString(R.string.shortcut_created, device.getName()), Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    private class TaskerItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Device device = adapter.getItem(position);
            Bundle extra = new Bundle();

            extra.putInt(Common.INTENT_EXTRA_DEVICE_ID, device.getID());

            Intent resultIntent = new Intent();

            resultIntent.putExtra(Common.TASKER_EXTRA_BUNDLE, extra);
            // Set text shown in Tasker.
            resultIntent.putExtra(Common.TASKER_EXTRA_STRING_BLURB, getString(R.string.shortcut_text, device.getName()));

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
