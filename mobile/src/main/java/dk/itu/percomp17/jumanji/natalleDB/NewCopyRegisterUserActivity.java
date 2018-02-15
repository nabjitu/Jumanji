package dk.itu.percomp17.jumanji.natalleDB;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import dk.itu.percomp17.jumanji.LocalDB;
import dk.itu.percomp17.jumanji.MyPermissions;
import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.SpeakerIdentificationAPI;
import dk.itu.percomp17.jumanji.toolbox.NetworkStatus;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class NewCopyRegisterUserActivity extends AppCompatActivity {
    private Button btnViewData;
    public Button GoToViewData;
    LocalDB ldb = LocalDB.getDB();
    DatabaseHelper dbHelper;

    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private static final int REQUEST_ACCESS_NETWORK_STATE_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

    //Debug Tags
    private static final String TAG = "RegisterUserAcc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        btnViewData = (Button) findViewById(R.id.bViewDataTest);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        MyPermissions mp = new MyPermissions();

        //VIIIIIKEEEEEERRRRRRRRRRR WUHUUUU
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                SpeakerIdentificationAPI speakerIdentificationAPI = new SpeakerIdentificationAPI();

                if (networkPermission()) {
                        Log.d(TAG, "-Network Granted-");

                    if (NetworkStatus.wifiConnected(getApplicationContext())) {
                        Log.d(TAG, "-Wifi ON-");

                        String azureID = speakerIdentificationAPI.createProfile();
                        Log.d(TAG, "-Azure Call Done-");

                        if (azureID != null) {
                            Log.d(TAG, "-Azure ID !NULL-");
                            System.out.println("User profile was created + " + azureID);
                            Toolbox.doToast("User profile was created + " + azureID, getApplicationContext());
                            System.out.print("AWESOMENESS");

                            dbHelper.addData(azureID, username, password);
                            if(!wasUserAdded(azureID, username, password)) Toolbox.doToast("User was not created", getApplicationContext());
                            else Toolbox.doToast("Wuhuuu.. User was created", getApplicationContext());

                        } else Toolbox.doToast("Could not create AzureID", getApplicationContext());
                    } else Toolbox.doToast("No network Connection", getApplicationContext());
                } else {
                    Toolbox.doToast("No Network Permission", getApplicationContext());
                    requestPermission();
                }
            }
        }); initViewData();
    }

    public void initViewData() {
        System.out.println("KAGEMAND");
        GoToViewData = (Button) findViewById(R.id.bViewDataTest);
        GoToViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(NewCopyRegisterUserActivity.this, ListDataActivity.class);
                startActivity(toy);
                System.out.println("KAGEMAND");
                /*Cursor data = dbHelper.getData();
                ArrayList<String> listData = new ArrayList<>();
                System.out.println("MINSENGERNICE2");
                while(data.moveToNext()){
                    //get the value from the database in column 1
                    //then add it to the ArrayList
                    System.out.println("MINSENGERNICE3");
                    System.out.println("MINSENGERNICE4" + data.getString(1));
                }*/
            }
        });
    }

    public boolean wasUserAdded(String azID, String un, String pw){
        Cursor data = dbHelper.getData();
        String azureID = null;
        String passwordFromDB = null;
        String usernameFromDB = null;
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            azureID = (data.getString(0));
            usernameFromDB = (data.getString(1));
            passwordFromDB = (data.getString(2));

        }
        if (azureID != null && un != null && pw != null){
            return true;
        }
        return false;
    }


    private boolean networkPermission() {
        String permission = "android.permission.REQUEST_INTERNET_PERMISSION";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return res != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_INTERNET_PERMISSION);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_ACCESS_NETWORK_STATE_PERMISSION);
    }
}
