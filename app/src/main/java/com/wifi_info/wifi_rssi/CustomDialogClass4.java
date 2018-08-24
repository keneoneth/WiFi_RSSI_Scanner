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

import java.util.ArrayList;

/**
 * Created by Kenneth on 24/8/2018.
 */

public class CustomDialogClass4 extends Dialog implements
        android.view.View.OnClickListener  {

    public Activity a;
    public Button confirm,clear;
    public TextView tv;
    public EditText edt;

    private ArrayList<String> placelist;

    public CustomDialogClass4(Activity a, ArrayList<String> placelist) {
        super(a);
        // TODO Auto-generated constructor stub
        this.a = a;
        this.placelist = (ArrayList<String>) placelist.clone();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_places_dialog);
        confirm = (Button) findViewById(R.id.bConfirm);
        clear = (Button) findViewById(R.id.bClear);
        confirm.setOnClickListener(this);
        clear.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.setPlaces);
        edt = (EditText) findViewById(R.id.addPlaces);

        String s ="Places list: \n";
        for(String str: placelist){
            s+=str+"\n";
        }
        tv.setText(s);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bConfirm:
                if(edt.getText().toString()!=null&&!edt.getText().toString().equals("")){
                    placelist.add(edt.getText().toString());
                    sendPlaceList();
                }
                dismiss();
                break;
            case R.id.bClear:
                Log.i("pressed","true");
                placelist.clear();
                sendPlaceList();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


    private void sendPlaceList() {
        MainActivity.placeList = (ArrayList<String>) placelist.clone();

    }
}
