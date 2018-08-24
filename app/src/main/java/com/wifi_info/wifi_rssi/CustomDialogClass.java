package com.wifi_info.wifi_rssi;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kenneth on 28/6/2018.
 */

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity a;
    public Button confirm,clear;
    public TextView tv;
    public EditText edt;

    private  ArrayList<String> routerlist;

    public CustomDialogClass(Activity a, ArrayList<String> routerlist) {
        super(a);
        // TODO Auto-generated constructor stub
        this.a = a;
        this.routerlist = (ArrayList<String>) routerlist.clone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_routers_dialog);
        confirm = (Button) findViewById(R.id.bConfirm);
        clear = (Button) findViewById(R.id.bClear);
        confirm.setOnClickListener(this);
        clear.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.setRouters);
        edt = (EditText) findViewById(R.id.addRouters);
        String s ="MAC Address list: \n";
        for(String str: routerlist){
            s+=str+"\n";
        }
        tv.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bConfirm:
                if(edt.getText().toString()!=null&&!edt.getText().toString().equals("")){
                    routerlist.add(edt.getText().toString());
                    sendRouterList();
                }
                dismiss();
                break;
            case R.id.bClear:
                Log.i("pressed","true");
                routerlist.clear();
                sendRouterList();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }




    private void sendRouterList() {
        MainActivity.targetRouters = (ArrayList<String>) routerlist.clone();

    }


}