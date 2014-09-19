package com.gmail.alexellingsen.unfoundwol.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.alexellingsen.unfoundwol.LazyAdapter;
import com.gmail.alexellingsen.unfoundwol.R;
import com.gmail.alexellingsen.unfoundwol.Settings;
import com.gmail.alexellingsen.unfoundwol.devices.Devices;

public class ShortcutActivity extends Activity {

    private Devices devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getThemeID(this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);

        devices = new Devices(getApplicationContext());
        devices.loadDevices();

        setupListView();
    }

    void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view.findViewById(R.id.list_item_name)).getText().toString();

                final Intent shortcutIntent = new Intent(ShortcutActivity.this, com.gmail.alexellingsen.unfoundwol.activities.WakePCActivity.class);
                final ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(ShortcutActivity.this, R.drawable.ic_launcher);
                final Intent intent = new Intent();

                shortcutIntent.putExtra("wake_pc", item);

                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Wake '" + item + "'");
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

                setResult(RESULT_OK, intent);

                Toast.makeText(ShortcutActivity.this, getString(R.string.shortcut_created, item), Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        LazyAdapter adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(devices.getDevices());
    }
}