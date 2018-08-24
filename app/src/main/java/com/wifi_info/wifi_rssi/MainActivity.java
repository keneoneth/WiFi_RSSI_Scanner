package com.wifi_info.wifi_rssi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.net.wifi.ScanResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity{
    protected WifiManager wm;
    protected  WifiManager.WifiLock wifiLock;
    protected BroadcastReceiver mReceiver;
    //TODO: setup Google Map API to provide request for GPS
   // protected GoogleMap googleMap;

    Context context;

    LocationManager locationManager;
    static TextView editText1;
    TextView editText2;
    Button bPreset;
    Button bScan;


    public static final int SCAN_DELAY = 1000;
    public static final int SCAN_INTERVAL = 1000;

    private static final int MENU_ITEM_CHOOSE = 1;
    private static final int MENU_ITEM_OUTPUT = 2;
    private static final int MENU_SET_TARGET_ROUTERS = 3;
    private static final int MENU_SIGNAL_FILTER = 4;
    private static final int MENU_PLACE_LIST = 5;

    protected static ArrayList<String> targetRouters = new ArrayList<String>();

    protected static String phoneModel = "";
    protected static int noOfScans = 100;
    protected static String scanTrial = "";
    protected static String testPoint = "";
    protected static ArrayList<String> placeList = new ArrayList<String>();
    protected static int WiFiSignalFilter = Integer.MIN_VALUE;

    private ArrayList<String> data = new ArrayList<String>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SubMenu sub = menu.addSubMenu(Menu.NONE, MENU_ITEM_CHOOSE, Menu.NONE, "Choose Item");
        //name change
        sub.add(Menu.NONE, MENU_ITEM_OUTPUT, Menu.NONE, "Output data");
        sub.add(Menu.NONE, MENU_SET_TARGET_ROUTERS, Menu.NONE, "Add target routers");
        sub.add(Menu.NONE, MENU_SIGNAL_FILTER, Menu.NONE, "Add signal filter");
        sub.add(Menu.NONE, MENU_PLACE_LIST, Menu.NONE, "Add places");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case MENU_ITEM_OUTPUT:
                //output the file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_WRITE_EXTERNAL_MEMORY);
                }else {
                    writeToFile(data, getApplicationContext());
                }
                return true;
            case MENU_PLACE_LIST:
                CustomDialogClass4 cdd4 = new CustomDialogClass4(this, placeList);
                cdd4.show();
                return true;
            case MENU_SET_TARGET_ROUTERS:
                //show up alert dialog to set target routers
                CustomDialogClass cdd = new CustomDialogClass(this, targetRouters);
                cdd.show();
                return true;
            case MENU_SIGNAL_FILTER:
                CustomDialogClass3 cdd3 = new CustomDialogClass3(this, WiFiSignalFilter);
                cdd3.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
      //  new tvThread().start();
        editText1 = (TextView) findViewById(R.id.et1);
        editText2 = (TextView) findViewById(R.id.et2);

        bPreset = (Button) findViewById(R.id.bPresetLocation);
        bScan = (Button) findViewById(R.id.bScan);

        setEditText1();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //TODO: Add default mac addresses
        if(targetRouters.size() == 0) {
            targetRouters.add("aa:aa:aa:aa:aa:aa");
        }
        //TODO: Add default places
        if(placeList.size() == 0){
            for(int i = 0; i < 3; i++){
                placeList.add("Place" + String.valueOf(i+1));
            }

        }

        bPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("test", noOfScans+"");
                CustomDialogClass2 cdd2 = new CustomDialogClass2( context, phoneModel, noOfScans, scanTrial, testPoint, placeList);
                cdd2.show();
            }
        });



            // &&locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        bScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                if (wm != null) {
                    wm.setWifiEnabled(true);
                    wifiLock =  wm.createWifiLock("tag");
                    wifiLock.setReferenceCounted(false);
                    if(!wifiLock.isHeld()) {
                        wifiLock.acquire();
                    }

                }

                if(returnWifiEnabled())
                {
                    if(!phoneModel.equals("") && noOfScans!=0 && !scanTrial.equals("") && !testPoint.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please Wait ...", Toast.LENGTH_LONG).show();

                        mReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context c, Intent intent) {
                                if (true) {
                                    onReceiveWifiScanResults(wm.getScanResults());
                                    Log.i("2. wifi scan results", String.valueOf(wm.getScanResults()));
                                    //List<ScanResult>	getScanResults()
                                    //Return the results of the latest access point scan.
                                    //https://developer.android.com/reference/android/net/wifi/WifiManager.html
                                }
                            }
                        };

                        registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

                        startWifiScan();

                    }else {
                        Toast.makeText(getApplicationContext(), "Please Preset Info.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    private boolean returnWifiEnabled(){
        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
    static public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static public final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_MEMORY = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
           wm.startScan();
        }
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_MEMORY
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            writeToFile(data,getApplicationContext());
        }
    }

    public void startWifiScan(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                   // googleMap.setMyLocationEnabled(true);
                   // googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

                }else{
                    wm.startScan();
                    //do something, permission was previously granted; or legacy device
                }

            }

        }, SCAN_DELAY, SCAN_INTERVAL);
    }


    public static final int MAX_SCAN_THREADS = 0;
    private int mScanThreadCount = 0;
    private static Thread t;
    private int count = 1;
   // private HashMap<String, Integer> mMeasurements;


    public void onReceiveWifiScanResults(final List<ScanResult> results) {

        // final ArrayList<Fingerprint> fingerprints = application.getFingerprintData(mSelectedMap);
        // calculating the location might take some time in case there are a lot of fingerprints (>10000),
        // so it's reasonable to limit scan thread count to make sure there are not too many of these threads
        // going on at the same time
        if(results.size() > 0 && mScanThreadCount <= MAX_SCAN_THREADS) {
            t = new Thread() {
                public void run() {


                    final StringBuilder listinfo = new StringBuilder();
                    if(count <= noOfScans) {
                        Log.i("scan results", String.valueOf(results));

                        listinfo.append("count：");
                        listinfo.append(count + "\n");
                        // HashMap<String, Integer> measurements = new HashMap<String, Integer>();
                        for (ScanResult result : results) {
                            //  measurements.put(result.BSSID, result.level);
                            if (targetRouters.size() == 0 && result.level > WiFiSignalFilter) {
                                data.add(String.valueOf(count));
                                listinfo.append("\nwifi MAC address：");
                                listinfo.append(result.BSSID);
                                data.add(result.BSSID);
                                listinfo.append("\nwifi RSSI：");
                                listinfo.append(result.level + "\n\n");
                                data.add(String.valueOf(result.level));

                            } else {
                                for (String tr : targetRouters) {
                                    if (result.BSSID.equals(tr) && result.level > WiFiSignalFilter) {
                                        data.add(String.valueOf(count));
                                        listinfo.append("\nwifi MAC address：");
                                        listinfo.append(result.BSSID);
                                        data.add(result.BSSID);
                                        listinfo.append("\nwifi RSSI：");
                                        listinfo.append(result.level + "\n\n");
                                        data.add(String.valueOf(result.level));
                                    }
                                }

                            }

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editText2.setText(listinfo.toString());
                            }
                        });
                        count++;
                    }else {
                        unregisterReceiver(mReceiver);
                        listinfo.setLength(0);
                        writeToFile(data, context);
                        count = 1;


                    }



                    // need to refresh map through updateHandler since only UI thread is allowed to touch its views
               //     sUpdateHandler.post(mRefreshMap);



                }
            };
            t.start(); // start new scan thread

        }

    }
    final static String strPhoneModel = "Phone Model: ";
    final static String strNoOfScans = "No. of Scans: ";
    final static String strRouterPoint = "Router Point: ";
    final static String strTestPoint = "Test Point: ";
    final static String strScanResults = "Wi-Fi Scan Results:";

    public static void setEditText1() {


        String s = "";
        s += strPhoneModel;
        s += (phoneModel.equals("")) ? "N/A" : phoneModel;
        s += "\n";

        s += strNoOfScans;
        s += (String.valueOf(noOfScans).equals("")) ? "N/A" : String.valueOf(noOfScans);
        s += "\n";

        s += strRouterPoint;
        s += (scanTrial.equals("")) ? "N/A" : scanTrial;
        s += "\n";

        s += strTestPoint;
        s += (testPoint.equals("")) ? "N/A" : testPoint;
        s += "\n" + "\n";

        s += strScanResults;

        editText1.setText(s);
    }


    private void writeToFile(ArrayList<String> data, Context context) {
        //TODO: android output csv

        String path =
                Environment.getExternalStorageDirectory() + File.separator  + "Wi-Fi Data";

        CSVWriter writer;
        // Create the folder.
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();}
        if(true) {
            // Create the file.
            String fileName = "";
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
            Date date = new Date();
            String formatDate = formatter.format(date);
            Log.i("fd", formatDate);
            fileName += formatDate+"_";
            fileName += phoneModel + "_";
            fileName += scanTrial + "_";
            fileName += testPoint + "_";
            fileName += String.valueOf(count-1);

            File file = new File(folder, fileName + ".csv");
            /*while (file.exists()) {
                file = new File(folder, "data" + count + ".csv");
                count++;
            }*/

            try {
                if(file.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(file);
                    writer = new CSVWriter(fileWriter);

                   /* String[] strPhoneModelArray = new String[]{ strPhoneModel, phoneModel};
                    writer.writeNext(strPhoneModelArray);

                    String[] strNoOfScansArray = new String[]{ strNoOfScans, String.valueOf(noOfScans)};
                    writer.writeNext(strNoOfScansArray);

                    String[] strRouterPointArray = new String[]{ strRouterPoint, String.valueOf(scanTrial)};
                    writer.writeNext(strRouterPointArray);

                    String[] strTestPointArray = new String[]{ strTestPoint, String.valueOf(testPoint)};
                    writer.writeNext(strTestPointArray);*/


                    String[] strColumnHeadsArray = new String[]{ "Count", "BSSID", "RSSI"};
                    writer.writeNext(strColumnHeadsArray);
                    String[] dataSegment = new String[3];
                    for(int i = 0; i < data.size(); i++){
                         dataSegment[i%3] = data.get(i);
                        if(i%3==2){
                            writer.writeNext(dataSegment);
                        }
                    }
                    writer.close();
                   /*FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(data);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();*/

                   // Clear all variables
                    data.clear();
                    testPoint = "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText2.setText("");
                            setEditText1();
                        }
                    });
                    Activity a = (Activity) context;
                    a.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Data output in progress ...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }


    private class tvThread extends Thread {
        @Override
        public void run() {
            while (true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //obtainListInfo();
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
