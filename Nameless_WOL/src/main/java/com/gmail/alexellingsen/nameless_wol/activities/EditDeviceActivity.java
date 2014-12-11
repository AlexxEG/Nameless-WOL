package com.gmail.alexellingsen.nameless_wol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.alexellingsen.nameless_wol.R;
import com.gmail.alexellingsen.nameless_wol.devices.Device;
import com.gmail.alexellingsen.nameless_wol.utils.Common;
import com.gmail.alexellingsen.nameless_wol.utils.ValidateUtils;

public class EditDeviceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_edit_device);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _edit_name = (EditText) findViewById(R.id.editText_name);
        _edit_host = (EditText) findViewById(R.id.editText_host);
        _edit_port = (EditText) findViewById(R.id.editText_port);
        _edit_mac = (EditText) findViewById(R.id.editText_mac);

        Intent i = getIntent();
        _device = i.getParcelableExtra(Common.INTENT_EXTRA_DEVICE);
        _editing = i.getBooleanExtra(Common.INTENT_EXTRA_EDITING, false);

        if (i.hasExtra(Common.INTENT_EXTRA_TITLE)) {
            // Allow the title to say for example "Add Device" or "Edit Device"
            this.setTitle(i.getStringExtra(Common.INTENT_EXTRA_TITLE));
        }

        if (_editing && _device != null) {
            _edit_name.setText(_device.getName());
            _edit_host.setText(_device.getHost());
            _edit_port.setText(String.valueOf(_device.getPort()));
            _edit_mac.setText(_device.getMac());
        }

        Button btnSave = (Button) findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave_Click();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Device _device;
    private boolean _editing = false;
    private EditText _edit_name;
    private EditText _edit_host;
    private EditText _edit_port;
    private EditText _edit_mac;

    private void btnSave_Click() {
        String name = _edit_name.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, this.getString(R.string.device_missing_name), Toast.LENGTH_SHORT).show();
            return;
        }

        String host = _edit_host.getText().toString();
        int port = Integer.parseInt(_edit_port.getText().toString());
        String mac = _edit_mac.getText().toString();

        // Validate everything
        if (!ValidateUtils.validateHost(host)) {
            Toast.makeText(this, this.getString(R.string.invalid_host), Toast.LENGTH_LONG).show();
            return;
        }
        if (!ValidateUtils.validateMAC(mac)) {
            Toast.makeText(this, this.getString(R.string.invalid_mac), Toast.LENGTH_LONG).show();
            return;
        }
        if (!ValidateUtils.validatePort(port)) {
            Toast.makeText(this, this.getString(R.string.invalid_port), Toast.LENGTH_LONG).show();
            return;
        }

        if (!_editing) {
            MainActivity._activity.addDevice(name, host, port, mac);
        } else {
            MainActivity._activity.editDevice(_device.getID(), name, host, port, mac);
        }

        this.finish();
    }
}
