package dk.itu.percomp17.jumanji.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.DebugDB;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperSoundbite;
import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.activities.audioRecorder.AudioRecorderActivity;
import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPLayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    public Button GoToUserAcc;
    public Button GoToLogin;
    public Button GoToTest;

    // Permission Variables
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 200;
    private boolean permissionToWriteExternalStoragedAccepted = false;

    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private boolean permissionToUseInternetAccepted = false;

    private static final int REQUEST_ACCESS_NETWORK_STATE = 200;
    private boolean permissionToAccessNetworkStateAccepted = false;

    DatabaseHelperSoundbite dbSB;

    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        dbSB = new DatabaseHelperSoundbite(mContext);

        //needs to be here
        //Following code snippet is to allow calling context from a class that is not an actity and from without oncreate methods.

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button startR = (Button) findViewById(R.id.startBtn);
        Button stopR = (Button) findViewById(R.id.finishBtn);
        Button playRecording = (Button) findViewById(R.id.playBtn);
        Button stopPlaying = (Button) findViewById(R.id.stopBtn);
        final TextView statusTV;
        statusTV = (TextView) findViewById(R.id.statusTextView);

        OUTPUT_FILE= Environment.getExternalStorageDirectory().getAbsolutePath()+"/audiorecorder.3gpp";


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        /*------Database------*/
        /*FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);*/
//
//        /*------GoTo User account button------*/
//        initUA();
//
//        /*------GoTo Login button------*/
        initLogin();
//
//        /*------GoTo Test button------*/
        initTest();

        /*------START recording BUTTON------*/

        startR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final TextView stv=statusTV;
                try {
                    beginRecording();
                    stv.setText("Recording");
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsString = sw.toString();
                    stv.setText("Error");
                }
            }
        });

        /*------STOP BUTTON------*/

        stopR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final TextView stv=statusTV;
                try {
                    stopRecording();
                    stv.setText("Stopped");
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsString = sw.toString();
                    statusTV.setText("Error");
                    Log.d("here","dd",e);
                }
            }
        });

        playRecording.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    playRecording();
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsString = sw.toString();
                    statusTV.setText("Error");
                }
            }
        });

        stopPlaying.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    stopPlayback();
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsString = sw.toString();
                    statusTV.setText("Error");
                }
            }
        });

        addTestDataToCOnTable();

    }

    private void stopPlayback() throws Exception{
        if(mediaPLayer != null)
            mediaPLayer.stop();
    }
    private void playRecording() throws IOException {
        ditchMediaPLayer();
        mediaPLayer = new MediaPlayer();
        mediaPLayer.setDataSource(OUTPUT_FILE);
        mediaPLayer.prepare();
        mediaPLayer.start();

    }

    private void ditchMediaPLayer() {
        if (mediaPLayer != null) {
            try{
                mediaPLayer.release();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if(recorder != null)
            recorder.stop();
    }
    private void beginRecording() throws Exception{
        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if (outFile.exists()) {
            outFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTPUT_FILE);
        recorder.prepare();
        recorder.start();
    }

    private void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.action_setting){
            Toast.makeText(MainActivity.this, "You have clicked on setting action menu", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.action_about_us){
            Toast.makeText(MainActivity.this, "You have clicked on about us action menu", Toast.LENGTH_SHORT).show();
//            goToUserAccount();
        }

        return super.onOptionsItemSelected(item);
    }

    // Requesting permission to RECORD_AUDIO
    //fra android.com


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

        //---ny permission ---
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                permissionToWriteExternalStoragedAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

        //---ny permission ---
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_INTERNET_PERMISSION:
                permissionToUseInternetAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_NETWORK_STATE:
                permissionToAccessNetworkStateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

//    /** Opens up new activity */
//    //Vedrører tollbaren som ligenu ikke bliver brugt /virker ikke.
//    public void goToUserAccount() {
//        Intent intent = new Intent(this, UserAccountActivity.class);
//        EditText editText = (EditText) findViewById(R.id.action_about_us);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }
//
//    //Har virkert, men virker ikke lige nu.
    public void addTestDataToCOnTable() {
        GoToUserAcc = (Button) findViewById(R.id.tvWelcomeMsg);
        GoToUserAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String azId = "f3a36b40-e2d8-4ae3-8651-1751da1e715e";
                String date = "05.12.2017";
                Integer from = (int) (long) Toolbox.getTimeLong();
                Integer to = (int) (long) Toolbox.getTimeLong()+ 45676;
                Integer conOrNot = 0;

                dbSB.addData(azId, date, from, to, conOrNot);
                Toolbox.doToast("Added the test data", mContext);
            }
        });
    }
    //Virker!
    public void initLogin() {
        GoToLogin = (Button) findViewById(R.id.Login);
        GoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(toy);

                System.out.println("HER KOMMER URL til database debug");
                //System.out.println(DebugDB.getAddressLog());
                DebugDB.getAddressLog();

            }
        });
    }
    //Virker!
    public void initTest() {
        GoToTest = (Button) findViewById(R.id.bTest);
        GoToTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(MainActivity.this, AudioRecorderActivity.class);
                startActivity(toy);
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }

}


