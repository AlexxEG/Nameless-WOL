package com.gmail.alexellingsen.nameless_wol;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.alexellingsen.nameless_wol.devices.Device;

public class DeviceView extends LinearLayout {
    private TextView _name_view;
    private TextView _host_port_view;
    private TextView _mac_view;

    public DeviceView(Context context) {
        super(context);
    }

    public DeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        _name_view = (TextView) findViewById(R.id.list_item_name);
        _host_port_view = (TextView) findViewById(R.id.list_item_host_port);
        _mac_view = (TextView) findViewById(R.id.list_item_mac);

        try {
            AssetManager am = getContext().getAssets();
            Typeface robotoLightTF = Typeface.createFromAsset(am, "fonts/Roboto-Light.ttf");

            _name_view.setTypeface(robotoLightTF);
            _host_port_view.setTypeface(robotoLightTF);
            _mac_view.setTypeface(robotoLightTF);
        } catch (Throwable throwable) {
            Log.e("nameless_wol", "Error", throwable);
        }
    }

    /**
     * Fills the DeviceView with the given Device's information.
     *
     * @param device The Device to show information from.
     */
    public void showDevice(Device device) {
        _name_view.setText(device.getName());
        _host_port_view.setText(device.getHost() + ":" + device.getPort());
        _mac_view.setText(device.getMac());
    }
}
