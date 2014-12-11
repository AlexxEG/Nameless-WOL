package com.gmail.alexellingsen.nameless_wol.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import com.gmail.alexellingsen.nameless_wol.LazyAdapter;
import com.gmail.alexellingsen.nameless_wol.R;
import com.gmail.alexellingsen.nameless_wol.devices.Device;
import com.gmail.alexellingsen.nameless_wol.devices.Devices;
import com.gmail.alexellingsen.nameless_wol.utils.Common;
import com.melnykov.fab.FloatingActionButton;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends ActionBarActivity {

    public static MainActivity _activity;

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

        // Store activity instance for easy access across activities
        _activity = this;

        // Initialize SQLite database
        Devices.initialize(this);

        // Setup main ListView
        this.setupListView();

        Log.d(Common.TAG, getSupportActionBar().getElevation() + " elevation");

        final ListView listView = (ListView) findViewById(R.id.listview);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        fabAdd.attachToListView(listView);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (MainActivity) v.getContext();
                Intent i = new Intent(activity, EditDeviceActivity.class);
                i.putExtra(Common.INTENT_EXTRA_TITLE, getString(R.string.device_new));
                activity.startActivity(i);
            }
        });
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
                Intent i = new Intent(this, EditDeviceActivity.class);
                i.putExtra(Common.INTENT_EXTRA_EDITING, true);
                i.putExtra(Common.INTENT_EXTRA_DEVICE, device);
                i.putExtra(Common.INTENT_EXTRA_TITLE, getString(R.string.device_edit));
                this.startActivity(i);
                break;
            case R.id.action_remove:
                adapter.remove(adapter.getItem(info.position));
                Devices.delete(device);
                break;
        }

        return true;
    }

    @Override
    public void onSupportContentChanged() {
        super.onSupportContentChanged();

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

    private void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        registerForContextMenu(listview);

        TextView emptyView = (TextView) findViewById(R.id.empty);

        listview.setEmptyView(emptyView);
        listview.setOnItemClickListener(getOnItemClickListener());

        adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(Devices.getAll());
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
