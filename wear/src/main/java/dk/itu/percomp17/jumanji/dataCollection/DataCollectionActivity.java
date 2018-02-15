package dk.itu.percomp17.jumanji.dataCollection;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.model.DataCollection;
import dk.itu.percomp17.jumanji.model.DataCollectionAzureDB;
import dk.itu.percomp17.jumanji.model.DataCollectionLocalDB;
import dk.itu.percomp17.jumanji.network.NetworkStatus;


public class DataCollectionActivity extends WearableActivity  implements View.OnClickListener {

    private static final String TAG = "DataCollectionActivity";

    // Local DB Refference
    private DataCollectionLocalDB sDataCollectionDB;
    private DataCollectionAzureDB sAzureDB;

    // UI Elements
    private BoxInsetLayout mContainerView;
    private Button mNewDataCollection;
    private TextView mStatusText;
    private Button mActivateButton;
    private Button mTest;
    private TextView mXtext;
    private TextView mYtext;
    private TextView mZtext;
    private TextView mIDtext;
    private Context context;

//    // Network
//    ConnectivityManager connectivityManager;

    // Permission Variables
    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 200;
    boolean network_granted;
    boolean network_state_granted;

    // Current DataCollection that we want to collect data for
    String dataCollectionID = "";

    // DataCollectionService variables
    private boolean dataCollectionServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "-onCreate-");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled(); //Will dim screen and remove color when not active

        context = getApplicationContext();

        // Check permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "-Access_Network_state: RequestPermission-");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_ACCESS_NETWORK_STATE);
        } else {
            network_state_granted = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "-Access_Internet: RequestPermission-");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        } else {
            network_granted = true;
        }


        // Get local DB refference
        sDataCollectionDB = DataCollectionLocalDB.getDataBase();
        sAzureDB = DataCollectionAzureDB.getsDataCollectionDB();

        // Inflate views
        mNewDataCollection = (Button) findViewById(R.id.button_new_data_collection);
        mNewDataCollection.setOnClickListener(this);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mStatusText = (TextView) findViewById(R.id.statusText);

        mActivateButton = (Button) findViewById(R.id.activateButton);
        mActivateButton.setOnClickListener(this);

        mTest = (Button) findViewById(R.id.button_test);
        mTest.setOnClickListener(this);

        mXtext = (TextView) findViewById(R.id.xTextView);
        mYtext = (TextView) findViewById(R.id.yTextView);
        mZtext = (TextView) findViewById(R.id.zTextView);
        mIDtext = (TextView) findViewById(R.id.idTextView);

        // Check if DataCollectionService is running and update UI
        dataCollectionServiceRunning = isServiceRunning(DataCollectionService.class);
        Log.d(TAG, "-dataCollectionServiceRunning = " + dataCollectionServiceRunning + "-");
        updateViewState();
    }

    // ===================================  GENERAL UI LOGIC ============================== //
    public void updateViewState() {

        if (!dataCollectionServiceRunning) {
            mStatusText.setText(R.string.statusText_DataCollectionNotActive);
            mActivateButton.setText(R.string.activateDataCollection);
            mStatusText.setBackgroundColor(getResources().getColor(R.color.off_red));
            mStatusText.setTextColor(getResources().getColor(R.color.off_red_text));

        } if (dataCollectionServiceRunning) {
            mStatusText.setText(R.string.statusText_DataCollectionActive);
            mActivateButton.setText(R.string.stopDataCollection);
            mStatusText.setBackgroundColor(getResources().getColor(R.color.on_green));
            mStatusText.setTextColor(getResources().getColor(R.color.on_green_text));
        }

        mIDtext.setText(dataCollectionID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.activateButton:
                if (!dataCollectionServiceRunning) {
                    startDataCollectionService();
                    updateViewState();

                } else {
                    stopDataCollectionService();
                    updateViewState();
                }

                break;

            case R.id.button_test:
                if (dataCollectionServiceRunning) {
                    float[] readings = sDataCollectionDB.getLatestReadings();
                    mXtext.setText(String.valueOf(readings[0]));
                    mYtext.setText(String.valueOf(readings[1]));
                    mZtext.setText(String.valueOf(readings[2]));
                }
                break;

            case R.id.button_new_data_collection:
                if (!network_granted && network_state_granted) {
                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                }

                dataCollectionID = "conversation_02";

//                try {
//                    dataCollectionID = "test6";
//                    dataCollectionID = NetworkStatus.wifiConnected(this) ? sAzureDB.geneterateID() : "test6";
//                }
//
//                catch (ExecutionException | InterruptedException e) { e.printStackTrace(); }

                updateViewState();
                break;

//            case R.id.button_upload_data_collection:
//                if (!network_granted && network_state_granted) {
//                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//
//                if (dataCollectionServiceRunning) stopDataCollectionService();
//                Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show();
//                DataCollection dataCollection = sDataCollectionDB.getDataCollection(dataCollectionID);
//                boolean result = sAzureDB.uploadDataCollection(dataCollection);
//                Toast.makeText(context, "Uploaded: " + result, Toast.LENGTH_LONG).show();
//                break;
        }
    }

    // ============================  DATA COLLECTION SERVICE : UI LOGIC   ================== //
    private void startDataCollectionService() {
        if (!dataCollectionServiceRunning) {
            Intent intent = new Intent(this, DataCollectionService.class);
            intent.putExtra("dataCollectionID", dataCollectionID);
            startService(intent);
            dataCollectionServiceRunning = true;
            Toast.makeText(context, "Activated", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopDataCollectionService() {
        if (dataCollectionServiceRunning) {
            dataCollectionServiceRunning = false;
            stopService(new Intent(this, DataCollectionService.class));
            Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();
            updateViewState();
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        }

        return false;
    }

    // ============================  PERMISSION REQUESTS   ============================ //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_NETWORK_STATE:
                network_state_granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "-onRequestPermissionResult: Access_Network_State = " + network_state_granted);
                break;
        }

        switch (requestCode) {
            case REQUEST_INTERNET_PERMISSION:
                network_granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "-onRequestPermissionResult: Access_Internet = " + network_granted);
        }
    }

    // ============================= LOCAL DATABASE : UI LOGIC ============================== //

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
