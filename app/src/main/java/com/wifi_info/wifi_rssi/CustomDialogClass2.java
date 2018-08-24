package com.wifi_info.wifi_rssi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Kenneth on 22/8/2018.
 */

public class CustomDialogClass2 extends Dialog implements
        android.view.View.OnClickListener{


    private Context c;
    private Button bConfirm;
    private Spinner setNoOfScan, setRouterPosition, setTestPosition;

    private String phoneModel;
    private int noOfScans;
    private String scanTrial;
    private String testPoint;
    private final String[] noOfScanItems = new String[]{"10", "50", "100", "500"};
    private final int noOfLCRooms = 17;
    private final int noOfTables = 10;
    private final int noOfTrials = 100; //TODO: determine no of trials
    private String[] roomItems;
    private final String[] scanTrialsItems = new String[noOfTrials];
    private EditText etPhoneModel;

    public CustomDialogClass2(Context context, String phoneModel, int noOfScans, String scanTrial, String testPoint, ArrayList<String> roomItems) {
        super((Activity) context);
        this.c = context;
        this.phoneModel = phoneModel;
        this.noOfScans = noOfScans;
        this.scanTrial = scanTrial;
        this.testPoint = testPoint;
        this.roomItems = roomItems.toArray(new String[roomItems.size()]);
        for (int i = 1; i <= noOfTrials; i++){
            scanTrialsItems[i-1] = "Scan " + String.valueOf(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_test_point_and_router_point_dialog);

        etPhoneModel = (EditText) findViewById(R.id.etPhoneModel);
        etPhoneModel = findViewById(R.id.etPhoneModel);

        etPhoneModel.setText(phoneModel);

        bConfirm = (Button) findViewById(R.id.bConfirm);
        bConfirm.setOnClickListener(this);

        setNoOfScan = findViewById(R.id.spNoOfScans);
        setRouterPosition = findViewById(R.id.spRouterSpinner);
        setTestPosition = findViewById(R.id.spTestSpinner);

        //No of scans
        ArrayAdapter<String> noOfScanAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_dropdown_item, noOfScanItems);
        setUpNoOfScans(noOfScanAdapter);

        ArrayAdapter<String> routerPositionAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_dropdown_item, scanTrialsItems);
        setUpRouterPosition(routerPositionAdapter);

        ArrayAdapter<String> testPositionAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_dropdown_item, roomItems);
        setUpTestPosition(testPositionAdapter);
    }

    private void setUpNoOfScans(ArrayAdapter<String> adapter){
        setNoOfScan.post(new Runnable() {
            @Override
            public void run() {
                setNoOfScan.setSelection(((ArrayAdapter)setNoOfScan.getAdapter()).getPosition(String.valueOf(noOfScans)));
            }
        });

        setNoOfScan.setAdapter(adapter);

        setNoOfScan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                //your code here
                noOfScans = Integer.parseInt(noOfScanItems[position]);
            }

            public void onNothingSelected(AdapterView<?> parentView)
            {
                //return nth
            }
        });
    }

    private void setUpRouterPosition(ArrayAdapter<String> adapter){
        if(!scanTrial.equals("")) {
            setRouterPosition.post(new Runnable() {
                @Override
                public void run() {
                    setRouterPosition.setSelection(((ArrayAdapter) setRouterPosition.getAdapter()).getPosition(scanTrial));
                }
            });
        }

        setRouterPosition.setAdapter(adapter);

        setRouterPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                //your code here
                scanTrial = scanTrialsItems[position];
            }

            public void onNothingSelected(AdapterView<?> parentView)
            {
                //return nth
            }
        });
    }

    private void setUpTestPosition(ArrayAdapter<String> adapter){
        if(!testPoint.equals("")) {
            setTestPosition.post(new Runnable() {
                @Override
                public void run() {
                    setTestPosition.setSelection(((ArrayAdapter) setTestPosition.getAdapter()).getPosition(testPoint));
                }
            });
        }

        setTestPosition.setAdapter(adapter);

        setTestPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                //your code here
                testPoint = roomItems[position];
            }

            public void onNothingSelected(AdapterView<?> parentView)
            {
                //return nth
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bConfirm:
                phoneModel = etPhoneModel.getText().toString();
                sendPresetInfo();
                MainActivity.setEditText1();
                dismiss();
                break;
        }
    }

    private void sendPresetInfo(){
        MainActivity.phoneModel = this.phoneModel;
        MainActivity.noOfScans = this.noOfScans;
        MainActivity.scanTrial = this.scanTrial;
        MainActivity.testPoint = this.testPoint;
    }


}
