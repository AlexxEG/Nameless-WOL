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

        Devices devices = new Devices(getApplicationContext());
        devices.loadDevices();

        Intent intent = this.getIntent();

        if (intent.hasExtra("wake_pc")) {
            String device = intent.getStringExtra("wake_pc");

            if (devices.containsKey(device)) {
                Device d = devices.get(device);

                if (d.canWake()) {
                    if (devices.get(device).wake()) {
                        Toast.makeText(this, getString(R.string.magic_packet_sending, device), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, getString(R.string.magic_packet_failed, device), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.device_info_error, device), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.device_not_found, device), Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }
}