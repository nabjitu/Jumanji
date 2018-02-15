package dk.itu.percomp17.jumanji.testing;

import android.Manifest;
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
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.activities.userAccount.UserAccountActivity;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class DataCollection extends AppCompatActivity {
    private MediaPlayer mediaPLayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    public Button GoToUserAcc;
    public Button GoToLogin;
    public Button GoToTest;

    // Permission Variables
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 200;
    private boolean permissionToWriteExternalStoragedAccepted = false;

    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private boolean permissionToUseInternetAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collec);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button startR = (Button) findViewById(R.id.Bstart);
        Button stopR = (Button) findViewById(R.id.Bstop);
        Button playRecording = (Button) findViewById(R.id.playBtn);
        Button stopPlaying = (Button) findViewById(R.id.stopBtn);
        final TextView statusTV;
        statusTV = (TextView) findViewById(R.id.statusTextView);

        OUTPUT_FILE= Environment.getExternalStorageDirectory().getAbsolutePath()+"/audiorecorder.3gpp";


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

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

        /*API constraints
        Container	WAV
        Encoding	PCM
        Rate	16K
        Sample Format	16 bit    OK
        Channels	Mono         OK
         */
        //recorder.setAudioSamplingRate(16);
        //channel 1 equals mono, 2 eqals stereo
        //recorder.setAudioChannels(1);

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
            Toast.makeText(DataCollection.this, "You have clicked on setting action menu", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.action_about_us){
            Toast.makeText(DataCollection.this, "You have clicked on about us action menu", Toast.LENGTH_SHORT).show();
            goToUserAccount();
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /** Opens up new activity */
    //Vedrører tollbaren som ligenu ikke bliver brugt /virker ikke.
    public void goToUserAccount() {
        Intent intent = new Intent(this, UserAccountActivity.class);
        EditText editText = (EditText) findViewById(R.id.action_about_us);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    //Metode som laver en writer.
    public void CreateTxt() throws  IOException {
        //concerning txt file
        int id = 0;
        String typeId = "S";

        EditText db = (EditText) findViewById(R.id.dbNoiseText);
        String dbString = db.getText().toString();

        EditText numPar = (EditText) findViewById(R.id.participatorsText);
        String npar = numPar.getText().toString();

        EditText conversationBool = (EditText) findViewById(R.id.CoverationText);
        String cBool = conversationBool.getText().toString();

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"dataForML.txt");
        try {
            PrintWriter writer = new PrintWriter("dataForML.txt", "UTF-8");
            writer.println(dbString + ", ");
            writer.println(npar + ", ");
            writer.println(cBool + ", ");
            writer.println(typeId + id + ", ");
            writer.println(Environment.getExternalStorageDirectory().getAbsolutePath()+"dataForML.txt");
            writer.println("Path to Acceleroeter CSV file ");
            writer.println("\n");
            writer.println();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
    //Metode som checker om writer findes.
    //Metode som skriver til writer.
}


