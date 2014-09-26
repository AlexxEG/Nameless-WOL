package com.gmail.alexellingsen.unfoundwol.tasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.gmail.alexellingsen.unfoundwol.R;
import com.gmail.alexellingsen.unfoundwol.devices.Device;
import com.gmail.alexellingsen.unfoundwol.devices.Devices;
import com.gmail.alexellingsen.unfoundwol.utils.Common;

public class FireReceiver extends BroadcastReceiver {

    private Context _context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Store Context for easy use in other methods
        _context = context;

        // Initialize SQLite database
        Devices.initialize(context);

        // Get Device ID
        int id = intent.getBundleExtra(Common.TASKER_EXTRA_BUNDLE).getInt(Common.INTENT_EXTRA_DEVICE_ID);

        // Get Device in SQLite database from ID
        Device device = Devices.get(id);

        if (device != null) {
            String name = device.getName();

            if (device.canWake()) {
                if (device.wake()) {
                    Toast.makeText(context, getString(R.string.magic_packet_sending, name), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.magic_packet_failed, name), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, getString(R.string.device_info_error, name), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, getString(R.string.device_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private String getString(int resId, Object... formatArgs) {
        return _context.getString(resId, formatArgs);
    }

}
