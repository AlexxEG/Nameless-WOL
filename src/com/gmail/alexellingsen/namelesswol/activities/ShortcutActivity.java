package com.gmail.alexellingsen.namelesswol.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.gmail.alexellingsen.namelesswol.LazyAdapter;
import com.gmail.alexellingsen.namelesswol.R;
import com.gmail.alexellingsen.namelesswol.devices.Device;
import com.gmail.alexellingsen.namelesswol.devices.Devices;
import com.gmail.alexellingsen.namelesswol.utils.Common;

public class ShortcutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);

        Devices.initialize(this);

        setupListView();
    }

    void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        final LazyAdapter adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(Devices.getAll());

        listview.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = adapter.getItem(position);

                final Intent shortcutIntent = new Intent(ShortcutActivity.this, com.gmail.alexellingsen.namelesswol.activities.WakePCActivity.class);
                final ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(ShortcutActivity.this, R.drawable.ic_launcher);
                final Intent intent = new Intent();

                shortcutIntent.putExtra(Common.INTENT_EXTRA_DEVICE_ID, device.getID());

                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_text, device.getName()));
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

                setResult(RESULT_OK, intent);

                Toast.makeText(ShortcutActivity.this, getString(R.string.shortcut_created, device.getName()), Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}