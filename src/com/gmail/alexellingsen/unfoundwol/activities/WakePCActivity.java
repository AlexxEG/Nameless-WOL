package com.gmail.alexellingsen.unfoundwol.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.gmail.alexellingsen.unfoundwol.R;
import com.gmail.alexellingsen.unfoundwol.devices.Device;
import com.gmail.alexellingsen.unfoundwol.devices.Devices;

public class WakePCActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Devices.init(this);

        Intent intent = this.getIntent();

        if (intent.hasExtra("wake_pc")) {
            String deviceName = intent.getStringExtra("wake_pc");
            Device device = Devices.find(deviceName);

            if (device != null) {
                if (device.canWake()) {
                    if (device.wake()) {
                        Toast.makeText(this, getString(R.string.magic_packet_sending, deviceName), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, getString(R.string.magic_packet_failed, deviceName), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.device_info_error, deviceName), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.device_not_found, deviceName), Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }
}