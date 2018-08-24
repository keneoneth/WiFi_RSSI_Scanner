package com.wifi_info.wifi_rssi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Kenneth on 24/8/2018.
 */

public class CustomDialogClass3 extends Dialog implements
        android.view.View.OnClickListener  {
    public Activity c;
    private Button confirm,clear;
    private TextView tv;
    private EditText edt;
    private int scanFilter;

    public CustomDialogClass3(Activity a, int scanFilter) {
        super(a);
        this.scanFilter = scanFilter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_scan_filter);
        confirm = (Button) findViewById(R.id.bConfirm);
        clear = (Button) findViewById(R.id.bClear);
        confirm.setOnClickListener(this);
        clear.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.setScanFilter);
        edt = (EditText) findViewById(R.id.addScanFilter);
        tv.setText("Scan Filter");
        if(scanFilter > Integer.MIN_VALUE){
            edt.setText(String.valueOf(-1 * scanFilter));
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bConfirm:
                if(edt.getText().toString()!=null&&!edt.getText().toString().equals("")){
                    scanFilter = -1* Integer.parseInt(edt.getText().toString());
                    sendScanFilter();
                }
                dismiss();
                break;
            case R.id.bClear:
                scanFilter = Integer.MIN_VALUE;
                sendScanFilter();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void sendScanFilter() {
        MainActivity.WiFiSignalFilter = scanFilter;

    }
}
