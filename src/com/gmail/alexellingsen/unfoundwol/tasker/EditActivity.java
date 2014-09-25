package com.gmail.alexellingsen.unfoundwol.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.gmail.alexellingsen.unfoundwol.LazyAdapter;
import com.gmail.alexellingsen.unfoundwol.R;
import com.gmail.alexellingsen.unfoundwol.devices.Device;
import com.gmail.alexellingsen.unfoundwol.devices.Devices;
import com.gmail.alexellingsen.unfoundwol.utils.Common;

public class EditActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);
        Devices.initialize(this);
        setupListView();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setupListView() {
        ListView listview = (ListView) findViewById(R.id.listview);

        final LazyAdapter adapter = new LazyAdapter(this);
        listview.setAdapter(adapter);
        adapter.addAll(Devices.getAll());

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("ConstantConditions")
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
        });
    }

}
