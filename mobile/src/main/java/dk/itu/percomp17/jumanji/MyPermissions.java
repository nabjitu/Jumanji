package dk.itu.percomp17.jumanji;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MyPermissions extends AppCompatActivity {
    private MediaPlayer mediaPLayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    public Button GoToUserAcc;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        OUTPUT_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecorder.3gpp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

    }
*/


    // Requesting permission to RECORD_AUDIO
    //fra android.com

    public static final String LOG_TAG = "AudioRecordTest";
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static String mFileName = null;
    public boolean permissionToRecordAccepted = false;
    public String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 200;
    public boolean permissionToWriteExternalStoragedAccepted = false;
    //private String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private boolean permissionToUseInternetAccepted = false;

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



}


