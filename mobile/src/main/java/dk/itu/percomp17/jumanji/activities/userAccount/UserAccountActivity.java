package dk.itu.percomp17.jumanji.activities.userAccount;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dk.itu.percomp17.jumanji.MyPermissions;

import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperSoundbite;
import dk.itu.percomp17.jumanji.natalleDB.ListDataActivity;
import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.Recording;
import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;
import dk.itu.percomp17.jumanji.activities.userAccount.registerUser.RegisterVoiceActivity;
import dk.itu.percomp17.jumanji.services.conversationDetection.ConversationDetectionService;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.SpeakerIdentificationAPI;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperSoundbite;
import dk.itu.percomp17.jumanji.natalleDB.ListDataActivity;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.SpeakerIdentificationAPI;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class UserAccountActivity extends AppCompatActivity {

    private MediaPlayer mediaPLayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    boolean isServiceRunning = false;

    public Button GoToRegisterVoice;
    public Button bTracking;
    private TextView mServiceStatus;
    DatabaseHelperSoundbite dbHelperSB;

    MyPermissions p = new MyPermissions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final TextView welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMsg);
        dbHelperSB = new DatabaseHelperSoundbite(getApplicationContext());

        welcomeMessage.setText(LoginActivity.getLoggedInCredentials().getUSername() + "'s account");

        mServiceStatus = (TextView) findViewById(R.id.service_status);

        //Get permission to record and store data
        ActivityCompat.requestPermissions(this, p.permissions, p.REQUEST_RECORD_AUDIO_PERMISSION);

        bTracking = (Button) findViewById(R.id.bStartTrack);
        bTracking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isServiceRunning) stopConversationDetectionService();
                if (!isServiceRunning) startConversationDetectionService();
            }
        });

        updateViewState();
        initHome();
        initRegisterVoice();
    }

    private void startConversationDetectionService(){
        Intent intent = new Intent(this, ConversationDetectionService.class);
        startForegroundService(intent);
        Toast.makeText(getApplicationContext(), "Activated", Toast.LENGTH_SHORT).show();
        updateViewState();
    }

    private void stopConversationDetectionService(){
        stopService(new Intent(this, ConversationDetectionService.class));
        Toast.makeText(getApplicationContext(), "Deactivated", Toast.LENGTH_SHORT).show();
        updateViewState();
    }

    private void updateViewState() {
        isServiceRunning = isServiceRunning(ConversationDetectionService.class);

        if (!isServiceRunning) {
            bTracking.setText("Start Tracking");
            mServiceStatus.setText("Not Tracking");
        }

        if (isServiceRunning) {
            bTracking.setText("Stop Tracking");
            mServiceStatus.setText("Tracking");
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        } return false;
    }

    public Button GoHome;
    public void initHome() {
        GoHome = (Button) findViewById(R.id.bStatistics);
        GoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(UserAccountActivity.this, StatisticsActivity.class);
                startActivity(toy);
//                getApplicationContext().deleteDatabase("people table");
//                getApplicationContext().deleteDatabase("profiles table");
//                getApplicationContext().deleteDatabase("profile table");
//                getApplicationContext().deleteDatabase("conversation table");
//                getApplicationContext().deleteDatabase("profil table");
//                Toolbox.doToast("Deleted tables", getApplicationContext());
            }
        });
    }


    public void initRegisterVoice() {
        GoToRegisterVoice = (Button) findViewById(R.id.bRegisterVoice);
        GoToRegisterVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(UserAccountActivity.this, RegisterVoiceActivity.class);
                startActivity(toy);
            }
        });
    }

    public String[] getUserDataFromConDB(){
        Cursor data = dbHelperSB.getData(); //Kun den der har dette azureId
        String[] variables = new String[4];
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            variables[0] = data.getString(0);
            variables[1] = data.getString(1);
            variables[2] = data.getString(2);
            variables[3] = data.getString(3);
            if(variables[0].equals(LoginActivity.getLoggedInCredentials().getAzProfileID())){
                return variables;
            }
        }
        return null;
    }




//    public void initListDataActivity() {
//        stopTracking = (Button) findViewById(R.id.bStopTrack);
//        stopTracking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toy = new Intent(UserAccountActivity.this, ListDataActivity.class);
//                startActivity(toy);
//
//                //Her skal den gemme loggen fra conversation tracking i databasen databaseHelperConversation.
//            }
//        });
//    }



//        startTracking.setOnClickListener(new View.OnClickListener(){
//        @Override
//        public void onClick(View view) {
//            try {
//                String azureID = LoginActivity.getLoggedInCredebtials().getAzProfileID();
//                String date = Toolbox.getDate();
//                Integer from = (int) (long) Toolbox.getTimeLong();
//                Integer to = (int) (long) Toolbox.getTimeLong();
//                to = to + 2894745;
//                System.out.println("");
//                dbHelperSB.addData(azureID, date, from, to, 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    });
}


