package com.gmail.alexellingsen.namelesswol.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.gmail.alexellingsen.namelesswol.R;
import com.gmail.alexellingsen.namelesswol.utils.ValidateUtils;
import com.gmail.alexellingsen.namelesswol.activities.MainActivity;
import com.gmail.alexellingsen.namelesswol.devices.Device;

public class DeviceDialog {

    private static Device _device;
    private static EditText _edit_name;
    private static EditText _edit_host;
    private static EditText _edit_port;
    private static EditText _edit_mac;

    private static AlertDialog create(Context context, boolean edit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View inflater = layoutInflater.inflate(R.layout.alert_dialog_device, null);

        _edit_name = (EditText) inflater.findViewById(R.id.editText_name);
        _edit_host = (EditText) inflater.findViewById(R.id.editText_host);
        _edit_port = (EditText) inflater.findViewById(R.id.editText_port);
        _edit_mac = (EditText) inflater.findViewById(R.id.editText_mac);

        _edit_mac.addTextChangedListener(new TextWatcher() {
            boolean formatting;
            int mStart;
            int mEnd;

            // ToDo: Doesn't work when at maxLength

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.mStart = -1;
                if (before == 0) {
                    mStart = start + count;
                    mEnd = Math.min(mStart + count, s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!formatting) {
                    if (mStart >= 0) {
                        formatting = true;
                        s.replace(mStart, mEnd, "");
                        formatting = false;
                    }
                }
            }
        });

        builder.setView(inflater);
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });

        if (edit) {
            // Add action button
            builder.setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // All of the fun happens inside the CustomListener now.
                    // I had to move it to enable data validation.
                }
            });

            _edit_name.setText(_device.getName());
            _edit_host.setText(_device.getHost());
            _edit_port.setText(String.valueOf(_device.getPort()));
            _edit_mac.setText(_device.getMac());
        } else {
            // Add action button
            builder.setPositiveButton(context.getString(R.string.add), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // All of the fun happens inside the CustomListener now.
                    // I had to move it to enable data validation.
                }
            });
        }

        return builder.create();
    }

    /**
     * Shows the 'New Device' dialog.
     *
     * @param context The AlertDialog's Context.
     * @return True no errors occur, false otherwise.
     */
    public static boolean show(Context context) {
        try {
            AlertDialog alertDialog = DeviceDialog.create(context, false);

            alertDialog.show();

            Button theButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            theButton.setOnClickListener(new CustomListener(alertDialog, context));
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /**
     * Shows the 'Edit Device' dialog.
     *
     * @param context The AlertDialog's Context.
     * @param device  The Device to edit.
     * @return True no errors occur, false otherwise.
     */
    public static boolean show(Context context, Device device) {
        try {
            _device = device;
            AlertDialog alertDialog = DeviceDialog.create(context, true);

            alertDialog.show();

            Button theButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            theButton.setOnClickListener(new CustomListener(alertDialog, context));
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private static class CustomListener implements View.OnClickListener {

        private final Context _context;
        private final Dialog _dialog;

        public CustomListener(Dialog dialog, Context context) {
            this._context = context;
            this._dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            String name = _edit_name.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(_context, _context.getString(R.string.device_missing_name), Toast.LENGTH_SHORT).show();
                return;
            }

            String host = _edit_host.getText().toString();
            int port = Integer.parseInt(_edit_port.getText().toString());
            String mac = _edit_mac.getText().toString();

            // Validate everything
            if (!ValidateUtils.validateHost(host)) {
                Toast.makeText(_context, _context.getString(R.string.invalid_host), Toast.LENGTH_LONG).show();
                return;
            }
            if (!ValidateUtils.validateMAC(mac)) {
                Toast.makeText(_context, _context.getString(R.string.invalid_mac), Toast.LENGTH_LONG).show();
                return;
            }
            if (!ValidateUtils.validatePort(port)) {
                Toast.makeText(_context, _context.getString(R.string.invalid_port), Toast.LENGTH_LONG).show();
                return;
            }

            MainActivity mainActivity = (MainActivity) _context;

            String btn = ((Button) view).getText().toString();

            if (btn.equals(_context.getString(R.string.add))) {
                mainActivity.addDevice(name, host, port, mac);
            } else if (btn.equals(_context.getString(R.string.save))) {
                mainActivity.editDevice(_device.getID(), name, host, port, mac);
            }

            _dialog.dismiss();
        }
    }
}
