package com.gmail.alexellingsen.nameless_wol.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.gmail.alexellingsen.nameless_wol.R;
import com.gmail.alexellingsen.nameless_wol.devices.Device;
import com.gmail.alexellingsen.nameless_wol.devices.Devices;
import com.gmail.alexellingsen.nameless_wol.utils.Common;

public class WakeDeviceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Devices.initialize(this);

        Intent intent = this.getIntent();

        if (intent.hasExtra(Common.INTENT_EXTRA_DEVICE_ID)) {
            int id = intent.getIntExtra(Common.INTENT_EXTRA_DEVICE_ID, -1);
            Device device = Devices.get(id);

            if (device != null) {
                if (device.canWake()) {
                    if (device.wake()) {
                        Toast.makeText(this, getString(R.string.magic_packet_sending, device.getName()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, getString(R.string.magic_packet_failed, device.getName()), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.device_info_error, device.getName()), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.device_not_found), Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }
}