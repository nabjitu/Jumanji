package dk.itu.percomp17.jumanji.testing;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.services.conversationDetection.ConversationDetectionService;

public class TestMain extends AppCompatActivity implements View.OnClickListener {

    Context context;

    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 200;
    private boolean permissionToWriteExternalStoragedAccepted = false;

    Intent mServiceIntent;

    Button start_sampler;
    Button stop_sampler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        context = getApplicationContext();

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        start_sampler = (Button) findViewById(R.id.start_sampler);
        start_sampler.setOnClickListener(this);

        stop_sampler = (Button) findViewById(R.id.stop_sampler);
        stop_sampler.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_sampler:
                if (!isServiceRunning(ConversationDetectionService.class)) startConversationDetectionService();
                break;

            case R.id.stop_sampler:
                if (isServiceRunning(ConversationDetectionService.class)) stopConversationDetectionService();
                break;
        }
    }



    private void startConversationDetectionService(){
        Intent intent = new Intent(this, ConversationDetectionService.class);
        startService(intent);
        Toast.makeText(context, "Activated", Toast.LENGTH_SHORT).show();
    }

    private void stopConversationDetectionService(){
        stopService(new Intent(this, ConversationDetectionService.class));
        Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        } return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

        //---ny permission ---
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                permissionToWriteExternalStoragedAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToWriteExternalStoragedAccepted) finish();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, ConversationDetectionService.class)); // !! Will force the Service to continue after app dies !!
        super.onDestroy();
    }
}
